import React, { useState } from 'react'
import {useAsync} from 'react-use'
import {FormikSelect} from "~/components/ui/form/inputs/select/Select";
import _isNil from 'lodash/isNil'

const getNavnOgIdListe = (data) => {
    let listeMedNavnOgId = []
    data.forEach(function (value) {
        if (!_isNil(value.fornavn)) {
            const mellomnavn = !_isNil(value.mellomnavn) ? " " + value.mellomnavn : "";
            const navnOgFnr = (value.fornavn + mellomnavn + " " + value.etternavn).toUpperCase()
                + ": " + value.fnr;
            listeMedNavnOgId.push({value: navnOgFnr, label: navnOgFnr});
        }
    })
    return listeMedNavnOgId;
}

const getNavnListe = (data) => {
    let listeMedNavn = []
    data.forEach(function (value) {
        if (!_isNil(value.fornavn)) {
            const mellomnavn = !_isNil(value.mellomnavn) ? " " + value.mellomnavn : "";
            const navn = value.fornavn + mellomnavn + " " + value.etternavn;
            listeMedNavn.push({value: navn.toUpperCase(), label: navn.toUpperCase()});
        }
    })
    return listeMedNavn;
}

export default function FasteDatasettSelect({name, label, endepunkt, type}) {
    if (!endepunkt) return false

    let optionsData = [];

    const state = useAsync(async () => {
        const response = await endepunkt();
        return response;
    }, [endepunkt]);

    if (state.value && state.value.data) {
        let optionHeight = 30;
        if (type === "fnr") {
            optionsData = state.value.data.map(e => ({value: e.fnr, label: e.fnr}));
        } else if (type === "navnOgId") {
            optionsData = getNavnOgIdListe(state.value.data);
            optionHeight = 50
        } else if (type === "navn") {
            optionsData = getNavnListe(state.value.data);
        }
        return <FormikSelect
            name={name}
            label={label}
            options={optionsData}
            size="large"
            optionHeight={optionHeight}
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


