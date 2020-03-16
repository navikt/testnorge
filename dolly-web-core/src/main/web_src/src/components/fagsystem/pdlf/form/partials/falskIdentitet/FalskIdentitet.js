import React from 'react'
import _get from 'lodash'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { DollyApi } from "~/service/Api";
import FasteDatasettSelect from "~/components/fasteDatasett/FasteDatasettSelect";

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
					endepunkt={ DollyApi.getFasteDatasettTPS }
					type = "navnOgId"
				/>
			)}
			{falskIdObj.identitetType === 'OMTRENTLIG' && (
				<div className="flexbox--flex-wrap">
					<FasteDatasettSelect
						name={ `${falskIdPath}.personnavn` }
						label="Navn"
						endepunkt={ DollyApi.getPersonnavn }
						type="navn"
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
