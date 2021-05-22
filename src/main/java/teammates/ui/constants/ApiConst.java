package teammates.ui.constants;

import com.fasterxml.jackson.annotation.JsonValue;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;

/**
 * Special constants used by the back-end.
 */
public enum ApiConst {
    // CHECKSTYLE.OFF:JavadocVariable
    COURSE_ID_MAX_LENGTH(FieldValidator.COURSE_ID_MAX_LENGTH),
    COURSE_NAME_MAX_LENGTH(FieldValidator.COURSE_NAME_MAX_LENGTH),
    STUDENT_NAME_MAX_LENGTH(FieldValidator.PERSON_NAME_MAX_LENGTH),
    SECTION_NAME_MAX_LENGTH(FieldValidator.SECTION_NAME_MAX_LENGTH),
    TEAM_NAME_MAX_LENGTH(FieldValidator.TEAM_NAME_MAX_LENGTH),
    EMAIL_MAX_LENGTH(FieldValidator.EMAIL_MAX_LENGTH),
    FEEDBACK_SESSION_NAME_MAX_LENGTH(FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH),

    CONTRIBUTION_POINT_NOT_SUBMITTED(Const.POINTS_NOT_SUBMITTED),
    CONTRIBUTION_POINT_NOT_INITIALIZED(Const.INT_UNINITIALIZED),
    CONTRIBUTION_POINT_NOT_SURE(Const.POINTS_NOT_SURE),
    CONTRIBUTION_POINT_EQUAL_SHARE(Const.POINTS_EQUAL_SHARE),
    NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED(Const.POINTS_NOT_SUBMITTED),
    RANK_OPTIONS_ANSWER_NOT_SUBMITTED(Const.POINTS_NOT_SUBMITTED),
    RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED(Const.POINTS_NOT_SUBMITTED),
    NO_VALUE(Const.POINTS_NO_VALUE),
    LOGS_RETENTION_PERIOD(Const.LOGS_RETENTION_PERIOD.toDays());
    // CHECKSTYLE.ON:JavadocVariable

    private final Object value;

    ApiConst(Object value) {
        this.value = value;
    }

    @JsonValue
    public Object getValue() {
        return value;
    }

}
