import React, { useState } from 'react'
import _get from 'lodash'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

export const FalskIdentitet = ({ formikBag }) => {
	const falskIdPath = 'pdlforvalter.falskIdentitet.rettIdentitet'
	const falskIdObj = formikBag.values.pdlforvalter.falskIdentitet.rettIdentitet

	const settIdentitetType = e => {
		if (e.value === 'UKJENT') {
			formikBag.setFieldValue(falskIdPath, { identitetType: e.value, rettIdentitetErUkjent: true })
		} else {
			formikBag.setFieldValue(falskIdPath, { identitetType: e.value })
		}
		return e.value
	}

	return (
		<Kategori title="Falsk identitet">
			<FormikSelect
				name={`${falskIdPath}.identitetType`}
				label="Opplysninger om rett identitet"
				options={Options('identitetType')}
				value={falskIdObj.identitetType}
				onChange={settIdentitetType}
				isClearable={false}
				size="grow"
			/>

			{falskIdObj.identitetType === 'ENTYDIG' && (
				<FormikTextInput
					name={`${falskIdPath}.rettIdentitetVedIdentifikasjonsnummer`}
					label="Identifikasjonsnummer"
				/>
			)}
			{falskIdObj.identitetType === 'OMTRENTLIG' && (
				<div>
					<FormikTextInput name={`${falskIdPath}.personnavn.fornavn`} label="Fornavn" />
					<FormikTextInput name={`${falskIdPath}.personnavn.mellomnavn`} label="Mellomnavn" />
					<FormikTextInput name={`${falskIdPath}.personnavn.etternavn`} label="Etternavn" />
					<FormikDatepicker name={`${falskIdPath}.foedselsdato`} label="Fødselsdato" />
					<FormikSelect
						name={`${falskIdPath}.kjoenn`}
						label="Kjønn"
						kodeverk="Kjønnstyper"
						isClearable={false}
					/>
					<FormikSelect
						name={`${falskIdPath}.statsborgerskap`}
						label="Statsborgerskap"
						kodeverk="Landkoder"
						isClearable={false}
					/>
				</div>
			)}
		</Kategori>
	)
}
