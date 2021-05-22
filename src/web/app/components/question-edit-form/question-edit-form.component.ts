import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CommonVisibilitySetting, FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { VisibilityStateMachine } from '../../../services/visibility-state-machine';
import {
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackTextQuestionDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';
import { VisibilityControl } from '../../../types/visibility-control';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { collapseAnim } from '../teammates-common/collapse-anim';
import { QuestionEditFormMode, QuestionEditFormModel } from './question-edit-form-model';

const FEEDBACK_PATH_PROPERTIES: Set<string> = new Set<string>([
  'giverType',
  'recipientType',
  'isUsingOtherFeedbackPath',
  'numberOfEntitiesToGiveFeedbackToSetting',
  'customNumberOfEntitiesToGiveFeedbackTo',
]);
const VISIBILITY_PROPERTIES: Set<string> = new Set<string>([
  'isUsingOtherVisibilitySetting',
  'showResponsesTo',
  'showGiverNameTo',
  'showRecipientNameTo',
  'commonVisibilitySettingName',
]);
const QUESTION_DETAIL_PROPERTIES: Set<string> = new Set<string>([
  'questionBrief',
  'questionDescription',
  'questionDetails',
  'questionNumber',
]);

/**
 * The question edit form component.
 */
@Component({
  selector: 'tm-question-edit-form',
  templateUrl: './question-edit-form.component.html',
  styleUrls: ['./question-edit-form.component.scss'],
  animations: [collapseAnim],
})
export class QuestionEditFormComponent implements OnInit {

  // enum
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;
  QuestionEditFormMode: typeof QuestionEditFormMode = QuestionEditFormMode;
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;
  NumberOfEntitiesToGiveFeedbackToSetting: typeof NumberOfEntitiesToGiveFeedbackToSetting =
      NumberOfEntitiesToGiveFeedbackToSetting;
  VisibilityControl: typeof VisibilityControl = VisibilityControl;
  FeedbackVisibilityType: typeof FeedbackVisibilityType = FeedbackVisibilityType;

  @Input()
  set formModel(model: QuestionEditFormModel) {
    this.model = model;

    this.commonFeedbackPaths = this.feedbackQuestionsService.getCommonFeedbackPaths(model.questionType);
    this.allowedFeedbackPaths = this.feedbackQuestionsService.getAllowedFeedbackPaths(model.questionType);
    this.visibilityStateMachine =
        this.feedbackQuestionsService.getNewVisibilityStateMachine(model.giverType, model.recipientType);
    this.commonFeedbackVisibilitySettings =
        this.feedbackQuestionsService.getCommonFeedbackVisibilitySettings(
            this.visibilityStateMachine, model.questionType);

    const visibilitySetting: {[TKey in VisibilityControl]: FeedbackVisibilityType[]} = {
      SHOW_RESPONSE: model.showResponsesTo,
      SHOW_GIVER_NAME: model.showGiverNameTo,
      SHOW_RECIPIENT_NAME: model.showRecipientNameTo,
    };
    this.visibilityStateMachine.applyVisibilitySettings(visibilitySetting);

    if (!model.isUsingOtherFeedbackPath) {
      // find if the feedback path is in the common feedback paths
      this.model.isUsingOtherFeedbackPath = true;
      if (this.commonFeedbackPaths.has(model.giverType) &&
          // tslint:disable-next-line:no-non-null-assertion
          this.commonFeedbackPaths.get(model.giverType)!.includes(model.recipientType)) {
        this.model.isUsingOtherFeedbackPath = false;
      }
    }
    if (!model.commonVisibilitySettingName && !model.isUsingOtherVisibilitySetting) {
      // find if the visibility settings is in the common visibility settings
      this.model.isUsingOtherVisibilitySetting = true;
      for (const commonVisibilityOption of this.commonFeedbackVisibilitySettings) {
        if (this.isSameSet(visibilitySetting.SHOW_RESPONSE, commonVisibilityOption.visibilitySettings.SHOW_RESPONSE)
            && this.isSameSet(visibilitySetting.SHOW_GIVER_NAME,
                commonVisibilityOption.visibilitySettings.SHOW_GIVER_NAME)
            && this.isSameSet(visibilitySetting.SHOW_RECIPIENT_NAME,
                commonVisibilityOption.visibilitySettings.SHOW_RECIPIENT_NAME)) {
          this.model.commonVisibilitySettingName = commonVisibilityOption.name;
          this.model.isUsingOtherVisibilitySetting = false;
          break;
        }
      }
    }
  }

  /**
   * Returns whether setting the custom feedback visibility is allowed.
   */
  get isCustomFeedbackVisibilitySettingAllowed(): boolean {
    return this.feedbackQuestionsService.isCustomFeedbackVisibilitySettingAllowed(this.model.questionType);
  }

  @Input()
  numOfQuestions: number = 0;

  @Input()
  formMode: QuestionEditFormMode = QuestionEditFormMode.EDIT;

  // if true, the question edit form is used for displaying of the question edit form only
  // no editing function will be available; the edit button will be hidden
  @Input()
  isDisplayOnly: boolean = false;

  model: QuestionEditFormModel = {
    feedbackQuestionId: '',

    questionNumber: 0,
    questionBrief: '',
    questionDescription: '',

    isQuestionHasResponses: false,

    questionType: FeedbackQuestionType.TEXT,
    questionDetails: {
      questionType: FeedbackQuestionType.TEXT,
      questionText: '',
    } as FeedbackTextQuestionDetails,

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.STUDENTS,

    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 1,

    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],

    commonVisibilitySettingName: '',

    isUsingOtherFeedbackPath: false,
    isUsingOtherVisibilitySetting: false,
    isEditable: false,
    isSaving: false,
    isCollapsed: false,
    isVisibilityChanged: false,
    isFeedbackPathChanged: false,
    isQuestionDetailsChanged: false,
  };

  @Output()
  formModelChange: EventEmitter<QuestionEditFormModel> = new EventEmitter();

  @Output()
  saveExistingQuestionEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  deleteCurrentQuestionEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  duplicateCurrentQuestionEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  discardExistingQuestionChangesEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  discardNewQuestionEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  createNewQuestionEvent: EventEmitter<void> = new EventEmitter();

  commonFeedbackPaths: Map<FeedbackParticipantType, FeedbackParticipantType[]> = new Map();

  allowedFeedbackPaths: Map<FeedbackParticipantType, FeedbackParticipantType[]> = new Map();

  commonFeedbackVisibilitySettings: CommonVisibilitySetting[] = [];

  visibilityStateMachine: VisibilityStateMachine;

  constructor(private feedbackQuestionsService: FeedbackQuestionsService,
              private simpleModalService: SimpleModalService) {
    this.visibilityStateMachine =
        this.feedbackQuestionsService.getNewVisibilityStateMachine(
            this.model.giverType, this.model.recipientType);
  }

  private isSameSet(setA: FeedbackVisibilityType[], setB: FeedbackVisibilityType[]): boolean {
    return setA.length === setB.length && setA.every((ele: FeedbackVisibilityType) => setB.includes(ele));
  }

  ngOnInit(): void {}

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: keyof QuestionEditFormModel,
                     data: QuestionEditFormModel[keyof QuestionEditFormModel]): void {
    this.formModelChange.emit({
      ...this.model,
      [field]: data,
      ...(!this.model.isVisibilityChanged && VISIBILITY_PROPERTIES.has(field)
        && { isVisibilityChanged: true }),
      ...(!this.model.isFeedbackPathChanged && FEEDBACK_PATH_PROPERTIES.has(field)
        && { isFeedbackPathChanged: true }),
      ...(!this.model.isQuestionDetailsChanged && QUESTION_DETAIL_PROPERTIES.has(field)
        && { isQuestionDetailsChanged: true }),
    });
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChangeBatch(obj: Partial<QuestionEditFormModel>): void {
    this.formModelChange.emit({
      ...this.model,
      ...obj,
      ...(!this.model.isVisibilityChanged
          && Object.keys(obj).some((key: string) => VISIBILITY_PROPERTIES.has(key))
          && { isVisibilityChanged: true }),
      ...(!this.model.isFeedbackPathChanged
          && Object.keys(obj).some((key: string) => FEEDBACK_PATH_PROPERTIES.has(key))
          && { isFeedbackPathChanged: true }),
      ...(!this.model.isQuestionDetailsChanged
          && Object.keys(obj).some((key: string) => QUESTION_DETAIL_PROPERTIES.has(key))
          && { isQuestionDetailsChanged: true }),
    });
  }

  /**
   * Change the {@code giverType} and {@code recipientType} and reset the visibility settings.
   */
  changeGiverRecipientType(giverType: FeedbackParticipantType, recipientType: FeedbackParticipantType): void {
    // check if current recipientType is allowed for giverType,
    // if not, set default recipientType to the first allowed type as default.
    /* tslint:disable-next-line: no-non-null-assertion */
    const allowedRecipientTypes: FeedbackParticipantType[] = this.allowedFeedbackPaths.get(giverType)!;
    let newRecipientType: FeedbackParticipantType = recipientType;
    if (allowedRecipientTypes.indexOf(recipientType) === -1) {
      newRecipientType = allowedRecipientTypes[0];
    }
    if (this.model.giverType === giverType && this.model.recipientType === newRecipientType) {
      // do not reset the visibility settings if reverting feedback path to preset template provided
      if (this.model.isUsingOtherFeedbackPath) {
        // remove the custom feedback if selecting a common feedback path
        this.triggerModelChangeBatch({
          isUsingOtherFeedbackPath: false,
        });
      }
    } else {
      this.triggerModelChangeBatch({
        giverType,
        recipientType: newRecipientType,
        commonVisibilitySettingName: 'Please select a visibility option',
        isUsingOtherFeedbackPath: false,
        isUsingOtherVisibilitySetting: false,
        showResponsesTo: [],
        showGiverNameTo: [],
        showRecipientNameTo: [],
      });
    }
  }

  /**
   * Applies the common visibility setting.
   */
  applyCommonVisibilitySettings(commonSettings: CommonVisibilitySetting): void {
    this.triggerModelChangeBatch({
      showResponsesTo: commonSettings.visibilitySettings.SHOW_RESPONSE,
      showGiverNameTo: commonSettings.visibilitySettings.SHOW_GIVER_NAME,
      showRecipientNameTo: commonSettings.visibilitySettings.SHOW_RECIPIENT_NAME,
      commonVisibilitySettingName: commonSettings.name,
      isUsingOtherVisibilitySetting: false,
    });
  }

  /**
   * Modifies visibility control of visibility type based on {@code isAllowed}.
   */
  modifyVisibilityControl(
      isAllowed: boolean, visibilityType: FeedbackVisibilityType, visibilityControl: VisibilityControl): void {
    if (isAllowed) {
      this.visibilityStateMachine.allowToSee(visibilityType, visibilityControl);
    } else {
      this.visibilityStateMachine.disallowToSee(visibilityType, visibilityControl);
    }
    this.triggerModelChangeBatch({
      showResponsesTo:
          this.visibilityStateMachine.getVisibilityTypesUnderVisibilityControl(VisibilityControl.SHOW_RESPONSE),
      showGiverNameTo:
          this.visibilityStateMachine.getVisibilityTypesUnderVisibilityControl(VisibilityControl.SHOW_GIVER_NAME),
      showRecipientNameTo:
          this.visibilityStateMachine.getVisibilityTypesUnderVisibilityControl(VisibilityControl.SHOW_RECIPIENT_NAME),
    });
  }

  /**
   * Helper methods to create a range.
   */
  range(num: number): number[] {
    const ranges: number[] = [];
    for (let i: number = 0; i < num; i += 1) {
      ranges.push(i);
    }
    return ranges;
  }

  /**
   * Handle event to discard changes users made.
   */
  discardChangesHandler(isNewQuestion: boolean): void {
    if (!this.model.isVisibilityChanged
      && !this.model.isFeedbackPathChanged
      && !this.model.isQuestionDetailsChanged) {
      this.discardChanges();
      return;
    }
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Discard unsaved ${isNewQuestion ? 'question' : 'edits'}?`, SimpleModalType.WARNING, 'Warning: Any unsaved changes will be lost');
    modalRef.result.then(() => {
      this.discardChanges();
    }, () => {});
  }

  private discardChanges(): void {
    if (this.formMode === QuestionEditFormMode.EDIT) {
      this.discardExistingQuestionChangesEvent.emit();
    }
    if (this.formMode === QuestionEditFormMode.ADD) {
      this.discardNewQuestionEvent.emit();
    }
  }

  /**
   * Saves the question.
   */
  saveQuestionHandler(): void {
    if (this.formMode === QuestionEditFormMode.EDIT) {
      const doChangesNeedWarning: boolean = this.model.isQuestionDetailsChanged
        || this.model.isVisibilityChanged
        || this.model.isFeedbackPathChanged;
      if (!this.model.isQuestionHasResponses || !doChangesNeedWarning) {
        this.saveExistingQuestionEvent.emit();
      } else if (this.model.isFeedbackPathChanged) {
        // warn user that editing feedback path will delete all messges
        const modalContent: string = `
            <p>You seem to have changed the feedback path settings of this question. Please note that changing the
            feedback path will cause <b>all existing responses to be deleted.</b> Proceed?</p>
        `;
        const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
            'Save the question?', SimpleModalType.DANGER, modalContent);
        modalRef.result.then(() => {
          this.saveExistingQuestionEvent.emit();
        }, () => {});
      } else if (this.model.isQuestionDetailsChanged) {
        // alert user that editing question may result in deletion of responses
        const modalContent: string = `
            <p>Editing question settings in a way that potentially affects the validity of existing responses <b> may
            cause all the existing responses for this question to be deleted.</b> Proceed?</p>
        `;
        const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
            'Save the question?', SimpleModalType.DANGER, modalContent);
        modalRef.result.then(() => {
          this.saveExistingQuestionEvent.emit();
        }, () => {});
      } else if (this.model.isVisibilityChanged) {
        // alert user that editing visibility options will not delete responses
        const modalContent: string = `
            <p>You seem to have changed the visibility settings of this question. Please note that <b>the existing
            responses will remain but their visibility will be changed as per the new visibility settings.</b>
            Proceed?</p>
        `;
        const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
            'Save the question?', SimpleModalType.WARNING, modalContent);
        modalRef.result.then(() => {
          this.saveExistingQuestionEvent.emit();
        }, () => {});
      }
    }
    if (this.formMode === QuestionEditFormMode.ADD) {
      this.createNewQuestionEvent.emit();
    }
  }

  /**
   * Handles event for duplicating the current question.
   */
  duplicateCurrentQuestionHandler(): void {
    this.duplicateCurrentQuestionEvent.emit();
  }

  /**
   * Handles event for deleting the current question.
   */
  deleteCurrentQuestionHandler(): void {
    this.deleteCurrentQuestionEvent.emit();
  }
}
