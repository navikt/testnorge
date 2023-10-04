import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import {
	getFieldSize,
	getInitialInntekt,
	kodeverkKeyToLabel,
} from '@/components/fagsystem/sigrunstubPensjonsgivende/utils'
import * as _ from 'lodash-es'

const getSkatteordningOptions = (skatteordning) => {
	return skatteordning?.map((item) => ({ value: item, label: _.capitalize(item) }))
}

const createInntektForm = (kodeverk, skatteordning, path) => {
	if (!kodeverk) {
		return null
		//TODO evt. lag fallback-data eller returner en feilmelding med kontakt dolly
	}

	const skatteordningOptions = getSkatteordningOptions(skatteordning)

	return Object.entries(kodeverk).map(([key, value]) => {
		const label = kodeverkKeyToLabel(key)
		const size = getFieldSize(label)
		if (key === 'skatteordning') {
			return (
				<FormikSelect
					name={`${path}.${key}`}
					key={`${path}.${key}`}
					label={label}
					options={skatteordningOptions}
					isClearable={false}
					size={size}
				/>
			)
		}
		if (value === 'Long') {
			return (
				<FormikTextInput
					name={`${path}.${key}`}
					key={`${path}.${key}`}
					label={label}
					type="number"
					size={size}
				/>
			)
		}
		if (value === 'Date') {
			return (
				<FormikDatepicker
					name={`${path}.${key}`}
					key={`${path}.${key}`}
					label={label}
					size={size}
				/>
			)
		}
		return (
			<FormikTextInput name={`${path}.${key}`} key={`${path}.${key}`} label={label} size={size} />
		)
		//TODO size utifra label length? :)
	})
}

export const PensjonsgivendeInntektForm = ({ path, formikBag, kodeverk, skatteordning }) => {
	const newEntry = getInitialInntekt(kodeverk, skatteordning)
	const inntektError = _.get(formikBag.errors, path)

	return (
		<FormikDollyFieldArray
			name={path}
			header="Inntekter"
			newEntry={newEntry}
			canBeEmpty={false}
			errorText={inntektError}
			nested
		>
			{(path, idx) => {
				return (
					<div className="flexbox--flex-wrap" key={idx}>
						{createInntektForm(kodeverk, skatteordning, path)}
					</div>
				)
			}}
		</FormikDollyFieldArray>
	)
}
