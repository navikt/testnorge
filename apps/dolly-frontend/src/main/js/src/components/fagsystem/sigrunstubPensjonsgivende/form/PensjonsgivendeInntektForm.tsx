import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import {
	usePensjonsgivendeInntektKodeverk,
	usePensjonsgivendeInntektSkatteordning,
} from '@/utils/hooks/useSigrunstub'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { kodeverkKeyToLabel } from '@/components/fagsystem/sigrunstubPensjonsgivende/utils'
import * as _ from 'lodash-es'

const getSkatteordningOptions = (skatteordning) => {
	return skatteordning?.map((item) => ({ value: item, label: _.capitalize(item) }))
}

const getInitialInntekt = (kodeverk, skatteordning) => {
	if (!kodeverk) {
		return null
		//TODO evt. lag fallback-data
	}

	const initialInntekt = {}

	for (const [key, value] of Object.entries(kodeverk)) {
		if (key === 'skatteordning') {
			initialInntekt[key] = skatteordning?.[0] || ''
		} else if (value === 'String') {
			initialInntekt[key] = ''
		} else if (value === 'Date') {
			initialInntekt[key] = new Date()
		} else initialInntekt[key] = null
	}

	return initialInntekt
}

const createInntektForm = (kodeverk, skatteordning, path) => {
	console.log('kodeverk: ', kodeverk) //TODO - SLETT MEG
	if (!kodeverk) {
		return null
		//TODO evt. lag fallback-data eller returner en feilmelding med kontakt dolly
	}

	const skatteordningOptions = getSkatteordningOptions(skatteordning)

	return Object.entries(kodeverk).map(([key, value]) => {
		const label = kodeverkKeyToLabel(key)
		if (key === 'skatteordning') {
			return (
				<FormikSelect
					name={`${path}.${key}`}
					key={`${path}.${key}`}
					label={label}
					options={skatteordningOptions}
					isClearable={false}
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
				/>
			)
		}
		if (value === 'Date') {
			return <FormikDatepicker name={`${path}.${key}`} key={`${path}.${key}`} label={label} />
		}
		return <FormikTextInput name={`${path}.${key}`} key={`${path}.${key}`} label={label} />
		//TODO size utifra label length? :)
	})
}

export const PensjonsgivendeInntektForm = ({ path, formikBag }) => {
	const { kodeverk } = usePensjonsgivendeInntektKodeverk()
	const { skatteordning } = usePensjonsgivendeInntektSkatteordning()

	const newEntry = getInitialInntekt(kodeverk, skatteordning)

	return (
		<FormikDollyFieldArray name={path} header="Inntekter" newEntry={newEntry} nested>
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
