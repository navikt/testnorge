import React, { useEffect, useState } from 'react'
import _get from 'lodash/get'
import { format, eachMonthOfInterval } from 'date-fns'
import Hjelpetekst from '~/components/hjelpetekst'
import { FormikSelect, DollySelect } from '~/components/ui/form/inputs/select/Select'
import { ArbeidKodeverk } from '~/config/kodeverk'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialValues,
	initialAmelding,
	initialArbeidsforhold,
	initialForenkletOppgjoersordning
} from '../initialValues'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import { ForenkletOppgjoersordningForm } from './forenkletOppgjoersordningForm'
import { Monthpicker } from '~/components/ui/form/inputs/monthpicker/Monthpicker'
import DollyKjede from '~/components/dollyKjede/DollyKjede'

export const AmeldingForm = ({ formikBag }) => {
	const type = _get(formikBag.values, 'aareg[0].arbeidsforholdstype')

	const [fom, setFom] = useState(null)
	const [tom, setTom] = useState(null)
	const [periode, setPeriode] = useState([])

	const [selectedIndex, setSelectedIndex] = useState(0)

	console.log('selectedIndex :>> ', selectedIndex)

	useEffect(() => {
		if (fom && tom) {
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
			// console.log('mnd :>> ', mnd)
			formikBag.setFieldValue(`aareg[0].amelding[${idx}]`, {
				maaned: mnd,
				arbeidsforhold: [initialArbeidsforhold]
				// ...initialArbeidsforhold
			})
		})
	}, [periode])

	const handleArbeidsforholdstypeChange = event => {
		formikBag.setFieldValue('aareg[0].arbeidsforholdstype', event.value)
		if (event.value === 'ordinaertArbeidsforhold' || event.value === 'maritimtArbeidsforhold') {
			formikBag.setFieldValue('aareg[0].amelding', initialAmelding)
			// formikBag.setFieldValue('aareg[0].arbeidsforhold', [initialArbeidsforhold])
		} else if (event.value === 'forenkletOppgjoersordning') {
			formikBag.setFieldValue('aareg[0].arbeidsforhold', [initialForenkletOppgjoersordning])
		}
	}

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
				)}
				{(type === 'ordinaertArbeidsforhold' || type === 'maritimtArbeidsforhold') && (
					<>
						<Monthpicker
							formikBag={formikBag}
							name="aareg[0].genererPeriode.fom"
							label="F.o.m. kalendermåned"
							date={fom}
							handleDateChange={dato => setFom(dato)}
						/>
						<Monthpicker
							formikBag={formikBag}
							name="aareg[0].genererPeriode.tom"
							label="T.o.m. kalendermåned"
							date={tom}
							handleDateChange={dato => setTom(dato)}
						/>
						{/* //TODO lag onClick for å fylle felter */}
						<NavButton mini onClick={null} disabled={!fom || !tom}>
							Fyll felter automatisk
						</NavButton>
						{periode.length > 0 && (
							<>
								{/* //TODO Når kjedekomponent er lenket må alle måneder endres ved endring av felt */}
								<DollyKjede
									objectList={periode}
									itemLimit={10}
									selectedIndex={selectedIndex}
									setSelectedIndex={setSelectedIndex}
								/>
								<FormikDollyFieldArray
									name={`aareg[0].amelding[${selectedIndex}].arbeidsforhold`}
									header="Arbeidsforhold"
									newEntry={initialArbeidsforhold}
									canBeEmpty={false}
								>
									{(path, idx) => (
										<ArbeidsforholdForm path={path} key={idx} formikBag={formikBag} />
									)}
								</FormikDollyFieldArray>
							</>
						)}
					</>
				)}
			</div>
		</>
	)
}
