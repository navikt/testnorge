import React, { useState } from 'react'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import { Formik } from 'formik'
import * as Yup from 'yup'
import { Radio } from 'nav-frontend-skjema'
import { subYears } from 'date-fns'
import _get from 'lodash/get'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { VisInntektsaar } from './visInntektsaar'
import { EnkeltinntektForm } from './enkeltinntektForm'

export const InntektsaarForm = ({ alleInntekter, setAlleInntekter }) => {
	const initialEnkeltinntekt = {
		inntektsaar: new Date().getFullYear(),
		inntektssted: 'Fastland',
		tjeneste: '',
		tekniskNavn: '',
		verdi: ''
	}
	const [enkeltinntekt, setEnkeltinntekt] = useState(initialEnkeltinntekt)

	return (
		<div>
			<DollySelect
				name="inntektsaar"
				label="År"
				options={yearOptions()}
				value={enkeltinntekt.inntektsaar}
				onChange={e => setEnkeltinntekt({ ...enkeltinntekt, inntektsaar: e.value })}
				isDisabled={alleInntekter.length > 0}
				isClearable={false}
			/>
			<DollySelect
				name="tjeneste"
				label="Tjeneste"
				options={Options('tjeneste')}
				value={enkeltinntekt.tjeneste}
				onChange={e =>
					setEnkeltinntekt({
						...enkeltinntekt,
						tjeneste: e.value,
						inntektssted: e.value === 'BEREGNET_SKATT' ? 'Fastland' : enkeltinntekt.inntektssted
					})
				}
				isDisabled={alleInntekter.length > 0}
			/>

			<Radio
				checked={enkeltinntekt.inntektssted === 'Fastland'}
				label="Fastland"
				name="fastland"
				onChange={e => setEnkeltinntekt({ ...enkeltinntekt, inntektssted: 'Fastland' })}
			/>

			{enkeltinntekt.tjeneste === 'BEREGNET_SKATT' ? (
				<Radio
					checked={enkeltinntekt.inntektssted === 'Svalbard'}
					label="Svalbard"
					name="svalbard"
					onChange={e => setEnkeltinntekt({ ...enkeltinntekt, inntektssted: 'Svalbard' })}
					disabled
				/>
			) : (
				<Radio
					checked={enkeltinntekt.inntektssted === 'Svalbard'}
					label="Svalbard"
					name="svalbard"
					onChange={e => setEnkeltinntekt({ ...enkeltinntekt, inntektssted: 'Svalbard' })}
				/>
			)}
			{
				<EnkeltinntektForm
					enkeltinntekt={enkeltinntekt}
					setEnkeltinntekt={setEnkeltinntekt}
					setAlleInntekter={setAlleInntekter}
				/>
			}
			{<VisInntektsaar alleInntekter={alleInntekter} setAlleInntekter={setAlleInntekter} />}
		</div>
	)
}

const yearOptions = () => {
	// Mulig å legge inn sigruninntekter fra 1968 og 5 år frem i tid
	let years = []
	for (let i = subYears(new Date(), -5).getFullYear(); i >= 1968; i--) {
		years.push({ value: i, label: i.toString() })
	}
	return years
}
