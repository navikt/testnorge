import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import {
	getFieldSize,
	getInitialInntekt,
	kodeverkKeyToLabel,
} from '@/components/fagsystem/sigrunstubPensjonsgivende/utils'
import * as _ from 'lodash-es'

const getSkatteordningOptions = (skatteordning) => {
	return skatteordning?.map((item) => ({ value: item, label: _.capitalize(item) }))
}

const createInntektForm = (kodeverk, skatteordning, path, formMethods) => {
	if (!kodeverk) {
		return null
	}

	const skatteordningOptions = getSkatteordningOptions(skatteordning)

	return Object.entries(kodeverk).map(([key, value]) => {
		const label = kodeverkKeyToLabel(key)
		const size = getFieldSize(label)
		if (key === 'skatteordning') {
			return (
				<FormSelect
					name={`${path}.${key}`}
					key={`${path}.${key}`}
					label={label}
					afterChange={() => formMethods.trigger('sigrunstubPensjonsgivende')}
					options={skatteordningOptions}
					isClearable={false}
					size={size}
				/>
			)
		}
		if (value === 'Long') {
			return (
				<FormTextInput
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
				<FormDatepicker name={`${path}.${key}`} key={`${path}.${key}`} label={label} size={size} />
			)
		}
		return (
			<FormTextInput name={`${path}.${key}`} key={`${path}.${key}`} label={label} size={size} />
		)
	})
}

export const PensjonsgivendeInntektForm = ({ path, formMethods, kodeverk, skatteordning }) => {
	const newEntry = getInitialInntekt(kodeverk, skatteordning)
	const inntektError =
		_.get(formMethods.formState.errors, path)?.message ||
		_.get(formMethods.formState.errors, `manual${path}`)?.message

	return (
		<FormDollyFieldArray
			name={path}
			header="Inntekter"
			newEntry={newEntry}
			canBeEmpty={false}
			errorText={inntektError}
			maxEntries={2}
			nested
		>
			{(path, idx) => {
				return (
					<div className="flexbox--flex-wrap sigrun-form" key={idx}>
						{createInntektForm(kodeverk, skatteordning, path, formMethods)}
					</div>
				)
			}}
		</FormDollyFieldArray>
	)
}
