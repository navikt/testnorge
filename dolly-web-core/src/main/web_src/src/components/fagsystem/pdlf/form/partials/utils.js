import _get from "lodash/get";
import _isNil from "lodash/isNil";

export const getPlaceholder = (values, selectedValuePath) =>{
    const selectedValue = _get(values, `${selectedValuePath}`)
    return !_isNil(selectedValue) && selectedValue!==""? selectedValue+'': "Velg.."
}