import React from 'react'
import { useAsync } from 'react-use'
import { FormikSelect } from "~/components/ui/form/inputs/select/Select";


export default function FasteDatasettSelect({ name, label, endepunkt }) {
    if (!endepunkt) return false

    let personData = [];

    const state = useAsync(async () => {
        const response = await endepunkt();
        return response;
    }, [endepunkt]);

    if (state.value && state.value.data) {
        personData = state.value.data.map(e =>  ({value: e.fnr, label: e.fnr}));
        return <FormikSelect
            name={name}
            label={label}
            options={personData}
        />
    }else if (state.error) {
        return <FormikSelect
            name={name}
            label={label}
            options={personData}
        />
    }

    return null
}

