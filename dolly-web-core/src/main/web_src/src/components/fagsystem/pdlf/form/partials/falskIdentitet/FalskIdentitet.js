import React from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { DollyApi } from "~/service/Api";
import FasteDatasettSelect from "~/components/fasteDatasett/FasteDatasettSelect";
import {getNavnOgFnrListe, getNavnListe} from "../filterMethods"
import {getPlaceholder} from "../utils"

export const FalskIdentitet = ({ formikBag }) => {
	const falskIdPath = 'pdlforvalter.falskIdentitet.rettIdentitet'
	const falskIdObj = formikBag.values.pdlforvalter.falskIdentitet.rettIdentitet

	const settIdentitetType = e => {
		if (e.value === 'UKJENT') {
			formikBag.setFieldValue(falskIdPath, { identitetType: e.value, rettIdentitetErUkjent: true })
		} else if (e.value === 'ENTYDIG') {
			formikBag.setFieldValue(falskIdPath, {
				identitetType: e.value,
				rettIdentitetVedIdentifikasjonsnummer: ''
			})
		} else {
			formikBag.setFieldValue(falskIdPath, {
				identitetType: e.value,
				foedselsdato: '',
				kjoenn: '',
				personnavn: '',
				statsborgerskap: ''
			})
		}
		return e.value
	}

	return (
		<div>
			<FormikSelect
				name={`${falskIdPath}.identitetType`}
				label="Opplysninger om rett identitet"
				options={Options('identitetType')}
				value={falskIdObj.identitetType}
				onChange={settIdentitetType}
				isClearable={false}
				size="medium"
			/>

			{falskIdObj.identitetType === 'ENTYDIG' && (
				<FasteDatasettSelect
					name={`${falskIdPath}.rettIdentitetVedIdentifikasjonsnummer`}
					label="Navn og identifikasjonsnummer"
					endepunkt={DollyApi.getFasteDatasettTPS}
					filterMethod={getNavnOgFnrListe}
					optionHeight={50}
					size="large"
				/>
			)}
			{falskIdObj.identitetType === 'OMTRENTLIG' && (
				<div className="flexbox--flex-wrap">
					<FasteDatasettSelect
						name={ `${falskIdPath}.personnavn` }
						label="Navn"
						endepunkt={DollyApi.getPersonnavn}
						filterMethod={getNavnListe}
						size="large"
						placeholder={getPlaceholder(formikBag.values, `${falskIdPath}.personnavn`)}
					/>
					<FormikDatepicker name={`${falskIdPath}.foedselsdato`} label="Fødselsdato" />
					<FormikSelect
						name={`${falskIdPath}.kjoenn`}
						label="Kjønn"
						options={Options('kjoennFalskIdentitet')}
						isClearable={false}
					/>
					<FormikSelect
						name={`${falskIdPath}.statsborgerskap`}
						label="Statsborgerskap"
						kodeverk="Landkoder"
						isClearable={false}
						isMulti={true}
						size="large"
					/>
				</div>
			)}
		</div>
	)
}
