import React from 'react'
import {useAsync} from 'react-use'
import {FormikSelect} from "~/components/ui/form/inputs/select/Select";
import _isNil from "lodash/isNil";

export default function FasteDatasettSelect({name, label, endepunkt, filterMethod, ...props}) {
    if (!endepunkt) return false

    let optionsData = [];

    const state = useAsync(async () => {
        const response = await endepunkt();
        return response;
    }, [endepunkt]);

    if (state.value && state.value.data) {
        optionsData = !_isNil(filterMethod) ? filterMethod(state.value.data): state.value.data
        return <FormikSelect
            name={name}
            label={label}
            options={optionsData}
            {...props}
        />

    } else if (state.error) {
        return <FormikSelect
            name={name}
            label={label}
            options={optionsData}
        />
    }

    return null
}


