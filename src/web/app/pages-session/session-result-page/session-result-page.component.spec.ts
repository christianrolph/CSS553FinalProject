import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../../services/auth.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { NavigationService } from '../../../services/navigation.service';
import { StudentService } from '../../../services/student.service';
import {
  AuthInfo, FeedbackContributionQuestionDetails, FeedbackContributionResponseDetails,
  FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestionType, FeedbackRubricQuestionDetails, FeedbackRubricResponseDetails,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus, NumberOfEntitiesToGiveFeedbackToSetting, QuestionOutput,
  RegkeyValidity,
  ResponseVisibleSetting, SessionResults,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { SingleStatisticsModule } from '../../components/question-responses/single-statistics/single-statistics.module';
import { StudentViewResponsesModule } from '../../components/question-responses/student-view-responses/student-view-responses.module';
import { QuestionTextWithInfoModule } from '../../components/question-text-with-info/question-text-with-info.module';
import { SessionResultPageComponent } from './session-result-page.component';
import Spy = jasmine.Spy;

describe('SessionResultPageComponent', () => {
  const testFeedbackSession: FeedbackSession = {
    feedbackSessionName: 'First Session',
    courseId: 'CS1231',
    timeZone: 'Asia/Singapore',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 1549095330000,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };

  const testInfo: AuthInfo = {
    masquerade: false,
    user: {
      id: 'user-id',
      isAdmin: false,
      isInstructor: true,
      isStudent: false,
    },
  };

  const testQuestion1: FeedbackQuestion = {
    feedbackQuestionId: 'feedbackQuestion1',
    questionNumber: 1,
    questionBrief: 'How well did team member perform?',
    questionDescription: '',
    questionDetails: {
      hasAssignedWeights: false,
      mcqWeights: [],
      mcqOtherWeight: 0,
      numOfMcqChoices: 3,
      mcqChoices: [
        '<p>Good</p>',
        '<p>Normal</p>',
        '<p>Bad</p>',
      ],
      otherEnabled: false,
      generateOptionsFor: 'NONE',
      questionType: FeedbackQuestionType.MCQ,
      questionText: 'How well did team member perform?',
    } as FeedbackMcqQuestionDetails,
    questionType: FeedbackQuestionType.MCQ,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  };

  const testQuestion2: FeedbackQuestion = {
    feedbackQuestionId: 'feedbackQuestion2',
    questionNumber: 2,
    questionBrief: 'Rate your teammates in contribution',
    questionDescription: '',
    questionDetails: {
      questionType: FeedbackQuestionType.CONTRIB,
      questionText: 'Rate your teammates in contribution',
      isNotSureAllowed: false,
    } as FeedbackContributionQuestionDetails,
    questionType: FeedbackQuestionType.CONTRIB,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  };

  const testQuestion3: FeedbackQuestion = {
    feedbackQuestionId: 'feedbackQuestion3',
    questionNumber: 3,
    questionBrief: 'Rate your teammates proficiency',
    questionDescription: '',
    questionDetails: {
      questionType: FeedbackQuestionType.RUBRIC,
      questionText: 'Rate your teammates proficiency',
      hasAssignedWeights: false,
      rubricWeightsForEachCell: [[]],
      numOfRubricChoices: 3,
      rubricChoices: ['Poor', 'Average', 'Good'],
      numOfRubricSubQuestions: 0,
      rubricSubQuestions: [],
      rubricDescriptions: [[]],
    } as FeedbackRubricQuestionDetails,
    questionType: FeedbackQuestionType.RUBRIC,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  };

  let component: SessionResultPageComponent;
  let fixture: ComponentFixture<SessionResultPageComponent>;
  let authService: AuthService;
  let navService: NavigationService;
  let studentService: StudentService;
  let feedbackSessionService: FeedbackSessionsService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        StudentViewResponsesModule,
        QuestionTextWithInfoModule,
        SingleStatisticsModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
      ],
      declarations: [SessionResultPageComponent],
      providers: [
        AuthService,
        NavigationService,
        StudentService,
        FeedbackSessionsService,
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({
              courseid: 'CS3281',
              fsname: 'Peer Feedback',
              key: 'reg-key',
            }),
          },
        },
      ],
    })
        .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionResultPageComponent);
    authService = TestBed.inject(AuthService);
    navService = TestBed.inject(NavigationService);
    studentService = TestBed.inject(StudentService);
    feedbackSessionService = TestBed.inject(FeedbackSessionsService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with session results are loading', () => {
    component.isFeedbackSessionResultsLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when session results failed to load', () => {
    component.isFeedbackSessionResultsLoading = false;
    component.hasFeedbackSessionResultsLoadingFailed = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with user that is logged in and using session link', () => {
    component.regKey = 'session-link-key';
    component.loggedInUser = 'alice';
    component.personName = 'alice';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with user that is not logged in and using session link', () => {
    component.regKey = 'session-link-key';
    component.loggedInUser = '';
    component.personName = 'alice';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with an open feedback session with no questions', () => {
    component.session = {
      courseId: 'CS3281',
      timeZone: 'UTC',
      feedbackSessionName: 'Peer Review 1',
      instructions: '',
      submissionStartTimestamp: 1555232400,
      submissionEndTimestamp: 1555233400,
      gracePeriod: 0,
      sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
      responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
      submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
      publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
      isClosingEmailEnabled: true,
      isPublishedEmailEnabled: true,
      createdAtTimestamp: 1555231400,
    };
    component.questions = [];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with feedback session with questions', () => {
    component.session = testFeedbackSession;
    component.questions = [
      {
        feedbackQuestion: testQuestion1,
        questionStatistics: '',
        allResponses: [],
        responsesToSelf: [],
        responsesFromSelf: [
          {
            isMissingResponse: false,
            responseId: 'resp-id-1',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient1',
            recipientTeam: 'team1',
            recipientSection: 'section1',
            responseDetails: {
              answer: 'Good',
              isOther: false,
              otherFieldContent: '',
            } as FeedbackMcqResponseDetails,
            instructorComments: [],
          },
        ],
        otherResponses: [[]],
      },
      {
        feedbackQuestion: testQuestion2,
        questionStatistics: '',
        allResponses: [],
        responsesToSelf: [
          {
            isMissingResponse: false,
            responseId: 'resp-id-2',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'giver1',
            recipientTeam: 'team1',
            recipientSection: 'section1',
            responseDetails: {
              answer: 120,
            } as FeedbackContributionResponseDetails,
            instructorComments: [],
          },
        ],
        responsesFromSelf: [
          {
            isMissingResponse: false,
            responseId: 'resp-id-3',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient2',
            recipientTeam: 'team2',
            recipientSection: 'section2',
            responseDetails: {
              answer: 110,
            } as FeedbackContributionResponseDetails,
            instructorComments: [],
          },
          {
            isMissingResponse: false,
            responseId: 'resp-id-4',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient3',
            recipientTeam: 'team2',
            recipientSection: 'section2',
            responseDetails: {
              answer: 100,
            } as FeedbackContributionResponseDetails,
            instructorComments: [],
          },
        ],
        otherResponses: [[]],
      },
      {
        feedbackQuestion: testQuestion3,
        questionStatistics: '',
        allResponses: [],
        responsesToSelf: [],
        responsesFromSelf: [
          {
            isMissingResponse: false,
            responseId: 'resp-id-5',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient3',
            recipientTeam: 'team2',
            recipientSection: 'section2',
            responseDetails: {
              answer: [1],
            } as FeedbackRubricResponseDetails,
            instructorComments: [
              {
                commentGiver: 'comment-giver-1',
                lastEditorEmail: 'comment@egeg.com',
                feedbackResponseCommentId: 1,
                commentText: 'this is a text',
                createdAt: 1402775804,
                lastEditedAt: 1402775804,
                isVisibilityFollowingFeedbackQuestion: true,
                showGiverNameTo: [],
                showCommentTo: [],
              },
            ],
          },
        ],
        otherResponses: [[]],
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should fetch auth info on init', () => {
    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));

    component.ngOnInit();

    expect(component.courseId).toEqual('CS3281');
    expect(component.feedbackSessionName).toEqual('Peer Feedback');
    expect(component.regKey).toEqual('reg-key');
    expect(component.loggedInUser).toEqual('user-id');
  });

  it('should verify allowed access and used reg key', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: true,
      isUsed: true,
      isValid: false,
    };
    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));
    spyOn(authService, 'getAuthRegkeyValidity').and.returnValue(of(testValidity));
    const navSpy: Spy = spyOn(navService, 'navigateByURLWithParamEncoding');

    component.ngOnInit();

    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1])
        .toEqual('/web/student/sessions/result');
  });

  it('should load info for unused reg key that is allowed', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: true,
      isUsed: false,
      isValid: false,
    };
    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));
    spyOn(authService, 'getAuthRegkeyValidity').and.returnValue(of(testValidity));
    spyOn(studentService, 'getStudent').and.returnValue(of({ name: 'student-name' }));
    spyOn(feedbackSessionService, 'getFeedbackSession').and.returnValue(of(testFeedbackSession));

    component.ngOnInit();

    expect(component.personName).toEqual('student-name');
    expect(component.session.courseId).toEqual('CS1231');
  });

  it('should fetch session results when loading feedback session', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: true,
      isUsed: false,
      isValid: false,
    };

    const testFeedbackSessionResult: SessionResults = {
      feedbackSession: testFeedbackSession,
      questions: [
        {
          feedbackQuestion: testQuestion1,
          questionStatistics: '',
          allResponses: [],
          responsesToSelf: [],
          responsesFromSelf: [],
          otherResponses: [],
        },
        {
          feedbackQuestion: testQuestion3,
          questionStatistics: '',
          allResponses: [],
          responsesToSelf: [],
          responsesFromSelf: [],
          otherResponses: [],
        },
        {
          feedbackQuestion: testQuestion2,
          questionStatistics: '',
          allResponses: [],
          responsesToSelf: [],
          responsesFromSelf: [],
          otherResponses: [],
        },
      ],
    };

    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));
    spyOn(authService, 'getAuthRegkeyValidity').and.returnValue(of(testValidity));
    spyOn(feedbackSessionService, 'getFeedbackSession').and.returnValue(of(testFeedbackSession));
    const fsSpy: Spy = spyOn(feedbackSessionService, 'getFeedbackSessionResults')
        .and.returnValue(of(testFeedbackSessionResult));

    component.ngOnInit();

    expect(fsSpy.calls.count()).toEqual(1);
    expect(fsSpy.calls.mostRecent().args[0]).toEqual({
      courseId: 'CS3281',
      feedbackSessionName: 'Peer Feedback',
      intent: Intent.STUDENT_RESULT,
      key: 'reg-key',
    });
    expect(component.questions.map((question: QuestionOutput) => question.feedbackQuestion.questionNumber))
        .toEqual([1, 2, 3]);
  });

  it('should deny access for reg key not belonging to logged in user', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: false,
      isUsed: false,
      isValid: true,
    };
    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));
    spyOn(authService, 'getAuthRegkeyValidity').and.returnValue(of(testValidity));
    const navSpy: Spy = spyOn(navService, 'navigateWithErrorMessage');

    component.ngOnInit();

    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/front');
    expect(navSpy.calls.mostRecent().args[2]).toEqual('You are not authorized to view this page.');
  });

  it('should deny access for invalid reg key', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: false,
      isUsed: false,
      isValid: false,
    };
    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));
    spyOn(authService, 'getAuthRegkeyValidity').and.returnValue(of(testValidity));
    const navSpy: Spy = spyOn(navService, 'navigateWithErrorMessage');

    component.ngOnInit();

    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/front');
    expect(navSpy.calls.mostRecent().args[2])
        .toEqual('You are not authorized to view this page.');
  });

  it('should navigate away when error occurs', () => {
    spyOn(authService, 'getAuthUser').and.returnValue(throwError({
      error: { message: 'This is error' },
    }));
    const navSpy: Spy = spyOn(navService, 'navigateWithErrorMessage');

    fixture.detectChanges();
    component.ngOnInit();

    expect(navSpy.calls.count()).toBe(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/front');
    expect(navSpy.calls.mostRecent().args[2])
        .toEqual('You are not authorized to view this page.');
  });

  it('should navigate to join course when user click on join course link', () => {
    component.regKey = 'reg-key';
    component.loggedInUser = 'user';
    const navSpy: Spy = spyOn(navService, 'navigateByURL');

    fixture.detectChanges();

    const btn: any = fixture.debugElement.nativeElement
        .querySelector('#join-course-btn');
    btn.click();

    expect(navSpy.calls.count()).toBe(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/join');
  });
})
;
