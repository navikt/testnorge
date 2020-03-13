import React from 'react'
import { useAsync } from 'react-use'
import { FormikSelect } from "~/components/ui/form/inputs/select/Select";
import _isNil from 'lodash/isNil'

export default function FasteDatasettSelect({ name, label, endepunkt, type }) {
    if (!endepunkt) return false

    let optionsData = [];

    const state = useAsync(async () => {
        const response = await endepunkt();
        return response;
    }, [endepunkt]);

    if (state.value && state.value.data) {
        let optionHeight = 30;
        if (type === "fnr"){
            optionsData = state.value.data.map(e =>  ({value: e.fnr, label: e.fnr}));
        }else if (type==="navnOgFnr"){
            state.value.data.forEach(function(value){
                if(!_isNil(value.fornavn)){
                    let navnOgFnr = (value.fornavn + " " + value.etternavn).toUpperCase()
                        + ": " + value.fnr;
                    optionsData.push({ value: navnOgFnr, label: navnOgFnr});
                }
            })
            optionHeight=50
        } else if (type==="navn"){
            state.value.data.forEach(function(value){
                if(!_isNil(value.fornavn)){
                    let navn = value.fornavn + " " + value.etternavn;
                    optionsData.push({ value: navn.toUpperCase(), label: navn.toUpperCase() });
                }
            })
        }
        return <FormikSelect
            name={name}
            label={label}
            options={optionsData}
            size="large"
            optionHeight={optionHeight}
        />


    }else if (state.error) {
        return <FormikSelect
            name={name}
            label={label}
            options={optionsData}
        />
    }

    return null
}


