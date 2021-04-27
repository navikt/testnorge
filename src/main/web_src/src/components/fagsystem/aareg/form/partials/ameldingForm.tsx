import React, { useEffect, useState } from 'react'
import _get from 'lodash/get'
import { format, eachMonthOfInterval } from 'date-fns'
import Hjelpetekst from '~/components/hjelpetekst'
import { FormikSelect, DollySelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { ArbeidKodeverk } from '~/config/kodeverk'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialValues,
	initialArbeidsforhold,
	initialForenkletOppgjoersordning
} from '../initialValues'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import { ForenkletOppgjoersordningForm } from './forenkletOppgjoersordningForm'

export const AmeldingForm = ({ formikBag }) => {
	const type = _get(formikBag.values, 'aareg[0].arbeidsforholdstype')
	// const [fom, setFom] = useState(_get(formikBag.values, 'aareg[0].genererPeriode.fom'))
	// const [tom, setTom] = useState(_get(formikBag.values, 'aareg[0].genererPeriode.tom'))
	const [fom, setFom] = useState(null)
	// console.log('fom :>> ', fom)
	const [tom, setTom] = useState(null)
	// console.log('tom :>> ', tom)
	const [periode, setPeriode] = useState([])
	console.log('periode :>> ', periode)
	console.log('formikBag :>> ', formikBag)

	useEffect(() => {
		if (fom && tom) {
			// console.log('fom xxx:>> ', fom)
			// console.log('tom xxx:>> ', tom)
			const maaneder = []
			const maanederTmp = eachMonthOfInterval({
				start: new Date(fom),
				end: new Date(tom)
			})
			maanederTmp.forEach(maaned => {
				maaneder.push(format(maaned, 'yyyy-MM'))
			})
			setPeriode(maaneder)
		}
	}, [fom, tom])

	//TODO hver måned kan ha flere arbeidsforhold, må kanskje wrappe alt i en måned-/amelding-komponent?
	useEffect(() => {
		periode.forEach((mnd, idx) => {
			console.log('mnd :>> ', mnd)
			formikBag.setFieldValue(`aareg[0].arbeidsforhold[${idx}]`, {
				maaned: mnd,
				...initialArbeidsforhold
			})
		})
	}, [periode])

	const handleArbeidsforholdstypeChange = event => {
		// console.log('event :>> ', event)
		formikBag.setFieldValue('aareg[0].arbeidsforholdstype', event.value)
		if (event.value === 'ordinaertArbeidsforhold' || event.value === 'maritimtArbeidsforhold') {
			formikBag.setFieldValue('aareg[0].arbeidsforhold', [initialArbeidsforhold])
		} else if (event.value === 'forenkletOppgjoersordning') {
			formikBag.setFieldValue('aareg[0].arbeidsforhold', [initialForenkletOppgjoersordning])
		}
	}

	// const handlePeriodeChange = (dato, type) => {
	// 	// const fom = _get(formikBag.values, 'aareg[0].genererPeriode.fom')
	// 	// const tom = _get(formikBag.values, 'aareg[0].genererPeriode.tom')
	// 	console.log('fom xxxxxx:>> ', fom)
	// 	// console.log('tom :>> ', tom)
	// 	console.log('dato :>> ', dato)
	// 	formikBag.setFieldValue(`aareg[0].genererPeriode.${type}`, dato)
	// 	// console.log(
	// 	// 	'_get(formikBag.values xxx fom :>> ',
	// 	// 	_get(formikBag.values, 'aareg[0].genererPeriode.fom')
	// 	// )
	// 	// console.log(
	// 	// 	'_get(formikBag.values xxx tom :>> ',
	// 	// 	_get(formikBag.values, 'aareg[0].genererPeriode.tom')
	// 	// )
	// 	if (type === 'fom') setFom(dato)
	// 	if (type === 'tom') setTom(dato)
	// 	// if (!tom) return null

	// 	// if (
	// 	// 	!_get(formikBag.values, 'aareg[0].genererPeriode.fom') ||
	// 	// 	!_get(formikBag.values, 'aareg[0].genererPeriode.tom')
	// 	// )
	// 	// 	return null
	// 	// console.log('new Date(fom) :>> ', new Date(fom))
	// 	// console.log('new Date(dato) :>> ', new Date(dato))
	// 	// console.log('fom :>> ', fom)
	// 	if (!fom) return null

	// 	let genererMaaneder = eachMonthOfInterval({
	// 		// start: new Date(_get(formikBag.values, 'aareg[0].genererPeriode.fom')),
	// 		start: new Date(fom),
	// 		// end: new Date(_get(formikBag.values, 'aareg[0].genererPeriode.tom'))
	// 		end: new Date(dato)
	// 	})
	// 	console.log('genererMaaneder :>> ', genererMaaneder)
	// }

	return (
		<>
			<div className="flexbox--flex-wrap">
				<h3>A-melding</h3>
				<Hjelpetekst hjelpetekstFor="A-melding">
					Om du har opprettet dine egne testorganisasjoner kan du sende A-meldinger til disse
					organisasjonene. Velg først hvilken type arbeidsforhold du ønsker å opprette, deretter kan
					du fylle ut resten av informasjonen.
				</Hjelpetekst>
			</div>
			<div className="flexbox--flex-wrap">
				<DollySelect
					name={`aareg[0].arbeidsforholdstype`}
					label="Type arbeidsforhold"
					kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
					size="large"
					isClearable={false}
					onChange={handleArbeidsforholdstypeChange}
					value={type}
				/>
				{type === 'forenkletOppgjoersordning' && (
					<ForenkletOppgjoersordningForm formikBag={formikBag} />
					// <>
					// 	<FormikDatepicker name={'aareg[0].ansettelsesPeriode.fom'} label="Ansatt fra" />
					// 	<FormikDatepicker name={'aareg[0].ansettelsesPeriode.tom'} label="Ansatt til" />
					// 	<FormikSelect
					// 		name={'aareg[0].yrke'}
					// 		label="Yrke"
					// 		kodeverk={ArbeidKodeverk.Yrker}
					// 		size="xxlarge"
					// 		isClearable={false}
					// 		optionHeight={50}
					// 	/>
					// </>
				)}
				{(type === 'ordinaertArbeidsforhold' || type === 'maritimtArbeidsforhold') && (
					//TODO fix så man kan velge måned
					<>
						<FormikDatepicker
							name={'aareg[0].genererPeriode.fom'}
							label="F.o.m. kalendermåned"
							// dateFormat="yyyy-MM"
							// onChange={dato => handlePeriodeChange(dato, 'fom')}
							onChange={dato => setFom(dato)}
							value={fom}
							showMonthYearPicker
						/>
						<FormikDatepicker
							name={'aareg[0].genererPeriode.tom'}
							label="T.o.m. kalendermåned"
							// dateFormat="yyyy-MM"
							// onChange={dato => handlePeriodeChange(dato, 'tom')}
							onChange={dato => setTom(dato)}
							value={tom}
							showMonthYearPicker
						/>
						{/* //TODO lag onClick for å fylle felter */}
						<NavButton mini onClick={null} disabled={!fom || !tom}>
							Fyll felter automatisk
						</NavButton>
						{periode.length > 0 && (
							// _get(formikBag.values, 'aareg[0].genererPeriode.fom') &&
							// 	_get(formikBag.values, 'aareg[0].genererPeriode.tom') && (
							// <div className="flexbox--flex-wrap">
							// 	<p>Arbeidsforhold heeer</p>
							// </div>
							<FormikDollyFieldArray
								name="aareg[0].arbeidsforhold"
								header="Arbeidsforhold"
								newEntry={initialArbeidsforhold}
								canBeEmpty={false}
							>
								{(path, idx) => <ArbeidsforholdForm path={path} key={idx} formikBag={formikBag} />}
							</FormikDollyFieldArray>
						)}
					</>
				)}
			</div>
		</>
	)
}
