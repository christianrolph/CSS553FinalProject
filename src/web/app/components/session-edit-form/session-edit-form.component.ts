import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbCalendar, NgbDateParserFormatter } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { TemplateSession } from '../../../services/feedback-sessions.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import {
  Course,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { FEEDBACK_SESSION_NAME_MAX_LENGTH } from '../../../types/field-validator';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { collapseAnim } from '../teammates-common/collapse-anim';
import { SessionEditFormDatePickerFormatter } from './session-edit-form-datepicker-formatter';
import { DateFormat, SessionEditFormMode, SessionEditFormModel } from './session-edit-form-model';

/**
 * Form to Add/Edit feedback sessions.
 */
@Component({
  selector: 'tm-session-edit-form',
  templateUrl: './session-edit-form.component.html',
  styleUrls: ['./session-edit-form.component.scss'],
  providers: [{ provide: NgbDateParserFormatter, useClass: SessionEditFormDatePickerFormatter }],
  animations: [collapseAnim],
})
export class SessionEditFormComponent implements OnInit {

  // enum
  SessionEditFormMode: typeof SessionEditFormMode = SessionEditFormMode;
  SessionVisibleSetting: typeof SessionVisibleSetting = SessionVisibleSetting;
  ResponseVisibleSetting: typeof ResponseVisibleSetting = ResponseVisibleSetting;

  // const
  FEEDBACK_SESSION_NAME_MAX_LENGTH: number = FEEDBACK_SESSION_NAME_MAX_LENGTH;

  @Input()
  model: SessionEditFormModel = {
    courseId: '',
    timeZone: 'UTC',
    courseName: '',
    feedbackSessionName: '',
    instructions: '',

    submissionStartTime: { hour: 0, minute: 0 },
    submissionStartDate: { year: 0, month: 0, day: 0 },
    submissionEndTime: { hour: 0, minute: 0 },
    submissionEndDate: { year: 0, month: 0, day: 0 },
    gracePeriod: 0,

    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTime: { hour: 0, minute: 0 },
    customSessionVisibleDate: { year: 0, month: 0, day: 0 },

    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    customResponseVisibleTime: { hour: 0, minute: 0 },
    customResponseVisibleDate: { year: 0, month: 0, day: 0 },

    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,

    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,

    templateSessionName: '',

    isSaving: false,
    isEditable: true,
    isDeleting: false,
    isCopying: false,
    hasVisibleSettingsPanelExpanded: false,
    hasEmailSettingsPanelExpanded: false,
  };

  @Output()
  modelChange: EventEmitter<SessionEditFormModel> = new EventEmitter();

  @Input()
  formMode: SessionEditFormMode = SessionEditFormMode.ADD;

  // add mode specific
  @Input()
  courseCandidates: Course[] = [];
  @Input()
  templateSessions: TemplateSession[] = [];

  // event emission
  @Output()
  addNewSessionEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  editExistingSessionEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  cancelEditingSessionEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  deleteExistingSessionEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  copyCurrentSessionEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  copyOtherSessionsEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  closeEditFormEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor(private simpleModalService: SimpleModalService, public calendar: NgbCalendar) { }

  ngOnInit(): void {
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    this.modelChange.emit({
      ...this.model,
      [field]: data,
    });
  }

  /**
   * Handles course Id change event.
   *
   * <p>Used in ADD mode.
   */
  courseIdChangeHandler(newCourseId: string): void {
    const course: Course | undefined = this.courseCandidates.find((c: Course) => c.courseId === newCourseId);

    if (course) {
      this.modelChange.emit({
        ...this.model,
        courseId: newCourseId,
        courseName: course.courseName,
        timeZone: course.timeZone,
      });
    }
  }

  /**
   * Gets the maximum date for a session to be visible based on the input model.
   */
  get maxDateForSessionVisible(): DateFormat {
    switch (this.model.responseVisibleSetting) {
      case ResponseVisibleSetting.LATER:
      case ResponseVisibleSetting.AT_VISIBLE:
        return this.model.submissionStartDate;
      case ResponseVisibleSetting.CUSTOM:
        const submissionStartDate: any = this.getMomentInstance(this.model.submissionStartDate);
        const responseVisibleDate: any = this.getMomentInstance(this.model.customResponseVisibleDate);
        if (submissionStartDate.isBefore(responseVisibleDate)) {
          return this.model.submissionStartDate;
        }

        return this.model.customResponseVisibleDate;
      default:
        return {
          year: 0,
          month: 0,
          day: 0,
        };
    }
  }

  /**
   * Gets the minimum date for responses to be visible based on the input model.
   */
  get minDateForResponseVisible(): DateFormat {
    switch (this.model.sessionVisibleSetting) {
      case SessionVisibleSetting.AT_OPEN:
        return this.model.submissionStartDate;
      case SessionVisibleSetting.CUSTOM:
        return this.model.customSessionVisibleDate;
      default:
        return {
          year: 0,
          month: 0,
          day: 0,
        };
    }
  }

  /**
   * Gets a moment instance from a date.
   */
  getMomentInstance(date: DateFormat): any {
    const inst: any = moment();
    inst.set('year', date.year);
    inst.set('month', date.month - 1); // moment month is from 0-11
    inst.set('date', date.day);
    return inst;
  }

  /**
   * Handles submit button click event.
   */
  submitFormHandler(): void {
    // resolve local date time to timestamp
    if (this.formMode === SessionEditFormMode.ADD) {
      this.addNewSessionEvent.emit();
    }

    if (this.formMode === SessionEditFormMode.EDIT) {
      this.editExistingSessionEvent.emit();
    }
  }

  /**
   * Handles cancel button click event.
   */
  cancelHandler(): void {
    this.simpleModalService.openConfirmationModal('Discard unsaved edit?',
        SimpleModalType.WARNING, 'Warning: Any unsaved changes will be lost.').result.then(() => {
          this.cancelEditingSessionEvent.emit();
        }, () => {});
  }

  /**
   * Handles delete current feedback session button click event.
   */
  deleteHandler(): void {
    this.simpleModalService.openConfirmationModal(`Delete the session <strong>${ this.model.feedbackSessionName }</strong>?`,
        SimpleModalType.WARNING,
        'The session will be moved to the recycle bin. This action can be reverted by going to the "Sessions" tab and restoring the desired session(s).',
    ).result.then(() => {
      this.deleteExistingSessionEvent.emit();
    }, () => {});
  }

  /**
   * Handles copy current feedback session button click event.
   */
  copyHandler(): void {
    this.copyCurrentSessionEvent.emit();
  }

  /**
   * Handles copy from other feedback sessions button click event.
   */
  copyOthersHandler(): void {
    this.copyOtherSessionsEvent.emit();
  }

  /**
   * Handles closing of the edit form.
   */
  closeEditFormHandler(): void {
    this.closeEditFormEvent.emit();
  }
}
