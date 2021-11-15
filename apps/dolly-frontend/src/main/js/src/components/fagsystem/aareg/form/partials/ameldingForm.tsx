import React, { ReactFragment, useState } from 'react'
import styled from 'styled-components'
import useBoolean from '~/utils/hooks/useBoolean'
import _get from 'lodash/get'
import _has from 'lodash/has'
import _set from 'lodash/set'
import _cloneDeep from 'lodash/cloneDeep'
import { add, eachMonthOfInterval, format } from 'date-fns'
import Hjelpetekst from '~/components/hjelpetekst'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { ArbeidKodeverk } from '~/config/kodeverk'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import Button from '~/components/ui/button/Button'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialAmelding,
	initialArbeidsforholdOrg,
	initialFartoy,
	initialForenkletOppgjoersordningOrg,
	initialPeriode,
} from '../initialValues'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import { Monthpicker } from '~/components/ui/form/inputs/monthpicker/Monthpicker'
import DollyKjede from '~/components/dollyKjede/DollyKjede'
import KjedeIcon from '~/components/dollyKjede/KjedeIcon'
import { FormikProps } from 'formik'
import { AaregListe, Amelding, KodeverkValue } from '~/components/fagsystem/aareg/AaregTypes'

interface AmeldingFormProps {
	formikBag: FormikProps<{ aareg: AaregListe }>
}

const KjedeContainer = styled.div`
	display: flex;
	flex-direction: row;
	align-items: center;
	width: 100%;
`

const Fyllknapp = styled(NavButton)`
	margin-bottom: 10px;
`

const Slettknapp = styled(Button)`
	margin: 10px 0;
`

export const AmeldingForm = ({ formikBag }: AmeldingFormProps): ReactFragment => {
	const arbeidsforholdstype = _get(formikBag.values, 'aareg[0].arbeidsforholdstype')

	const fom = _get(formikBag.values, 'aareg[0].genererPeriode.fom')
	const tom = _get(formikBag.values, 'aareg[0].genererPeriode.tom')
	const periode = _get(formikBag.values, 'aareg[0].genererPeriode.periode')
	const ameldinger = _get(formikBag.values, 'aareg[0].amelding')

	const [erLenket, setErLenket, setErIkkeLenket] = useBoolean(true)
	const [selectedIndex, setSelectedIndex] = useState(0)

	const handlePeriodeChange = (dato: string, type: string) => {
		formikBag.setFieldValue(`aareg[0].genererPeriode.${type}`, dato)

		if ((type === 'tom' && fom) || (type === 'fom' && tom)) {
			const maanederPrev: Array<Amelding> = _get(formikBag.values, 'aareg[0].amelding')
			const maaneder: Array<string> = []
			const maanederTmp = eachMonthOfInterval({
				start: new Date(type === 'fom' ? dato : fom),
				end: new Date(type === 'tom' ? dato : tom),
			})
			maanederTmp.forEach((maaned) => {
				maaneder.push(format(maaned, 'yyyy-MM'))
			})
			formikBag.setFieldValue('aareg[0].genererPeriode.periode', maaneder)

			if (maaneder.length < maanederPrev.length) {
				const maanederFiltered = maanederPrev.filter((maaned) => maaneder.includes(maaned.maaned))
				formikBag.setFieldValue('aareg[0].amelding', maanederFiltered)
			} else {
				maaneder.forEach((mnd, idx) => {
					const currMaaned = _get(formikBag.values, 'aareg[0].amelding').find(
						(element: Amelding) => element.maaned == mnd
					)
					formikBag.setFieldValue(`aareg[0].amelding[${idx}]`, {
						maaned: mnd,
						arbeidsforhold: currMaaned
							? currMaaned.arbeidsforhold
							: arbeidsforholdstype === 'forenkletOppgjoersordning'
							? [initialForenkletOppgjoersordningOrg]
							: [initialArbeidsforholdOrg],
					})
					if (arbeidsforholdstype === 'maritimtArbeidsforhold') {
						formikBag.setFieldValue(
							`aareg[0].amelding[${idx}].arbeidsforhold[0].fartoy`,
							initialFartoy
						)
					}
				})
			}
		}
	}

	const handleArbeidsforholdstypeChange = (event: KodeverkValue) => {
		const amelding = _get(formikBag.values, 'aareg[0].amelding')
		const ameldingClone = _cloneDeep(amelding)

		if (event.value === 'forenkletOppgjoersordning') {
			ameldingClone.forEach((maaned: string, idx: number) => {
				_set(ameldingClone[idx], 'arbeidsforhold', [initialForenkletOppgjoersordningOrg])
			})
		} else {
			ameldingClone.forEach((maaned: string, idx: number) => {
				if (arbeidsforholdstype === 'forenkletOppgjoersordning' || arbeidsforholdstype === '') {
					_set(ameldingClone[idx], 'arbeidsforhold', [initialArbeidsforholdOrg])
				}
				if (event.value === 'maritimtArbeidsforhold') {
					maaned.arbeidsforhold.forEach((arbforh, id) => {
						_set(ameldingClone[idx], `arbeidsforhold[${id}].fartoy`, initialFartoy)
					})
				} else {
					maaned.arbeidsforhold.forEach((arbforh, id) => {
						_set(ameldingClone[idx], `arbeidsforhold[${id}].fartoy`, undefined)
					})
				}
			})
		}
		formikBag.setFieldValue('aareg[0].amelding', ameldingClone)
		formikBag.setFieldValue('aareg[0].arbeidsforholdstype', event.value)
	}

	const handleNewEntry = () => {
		ameldinger.forEach((maaned: Amelding, idMaaned: number) => {
			if (!erLenket && idMaaned != selectedIndex) return
			const currArbeidsforhold = _get(
				formikBag.values,
				`aareg[0].amelding[${idMaaned}].arbeidsforhold`
			)
			const nyttArbeidsforhold =
				arbeidsforholdstype === 'forenkletOppgjoersordning'
					? initialForenkletOppgjoersordningOrg
					: arbeidsforholdstype === 'maritimtArbeidsforhold'
					? { ...initialArbeidsforholdOrg, fartoy: initialFartoy }
					: initialArbeidsforholdOrg
			formikBag.setFieldValue(`aareg[0].amelding[${idMaaned}].arbeidsforhold`, [
				...currArbeidsforhold,
				nyttArbeidsforhold,
			])
		})
	}

	const handleRemoveEntry = (idArbeidsforhold: number) => {
		ameldinger.forEach((maaned: Amelding, idMaaned: number) => {
			if (!erLenket && idMaaned != selectedIndex) return
			const currArbeidsforhold = _get(
				formikBag.values,
				`aareg[0].amelding[${idMaaned}].arbeidsforhold`
			)
			currArbeidsforhold.splice(idArbeidsforhold, 1)
			formikBag.setFieldValue(`aareg[0].amelding[${idMaaned}].arbeidsforhold`, currArbeidsforhold)
		})
	}

	const handleFjernMaaned = () => {
		const currAmelding = _get(formikBag.values, 'aareg[0].amelding')
		currAmelding.splice(selectedIndex, 1)
		formikBag.setFieldValue('aareg[0].amelding', currAmelding)

		const nyPeriode = periode
		nyPeriode.splice(selectedIndex, 1)
		formikBag.setFieldValue('aareg[0].genererPeriode.periode', nyPeriode)

		if (periode.length === 1) {
			setSelectedIndex(0)
		} else if (selectedIndex > 0) {
			setSelectedIndex(selectedIndex - 1)
		} else {
			setSelectedIndex(selectedIndex)
		}
	}

	const feilmelding = () => {
		if (
			!_get(formikBag.values, 'aareg[0].arbeidsforholdstype') &&
			_has(formikBag.touched, 'aareg[0].arbeidsforholdstype')
		) {
			return {
				feilmelding: _get(formikBag.errors, 'aareg[0].arbeidsforholdstype'),
			}
		}
	}

	return (
		<>
			<div className="flexbox--align-center">
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
					size="large-plus"
					isClearable={false}
					onChange={handleArbeidsforholdstypeChange}
					value={arbeidsforholdstype}
					feil={feilmelding()}
				/>
				<>
					<Monthpicker
						formikBag={formikBag}
						name="aareg[0].genererPeriode.fom"
						label="F.o.m. kalendermåned"
						date={fom}
						handleDateChange={(dato: string) => handlePeriodeChange(dato, 'fom')}
						minDate={add(new Date(), { years: -25 })}
					/>
					<Monthpicker
						formikBag={formikBag}
						name="aareg[0].genererPeriode.tom"
						label="T.o.m. kalendermåned"
						date={tom}
						handleDateChange={(dato: string) => handlePeriodeChange(dato, 'tom')}
						minDate={add(new Date(), { years: -25 })}
					/>
					<div className="flexbox--full-width">
						<div className="flexbox--flex-wrap">
							{/* //TODO lag onClick for å fylle felter */}
							{/* <Fyllknapp mini onClick={null} disabled={!fom || !tom}> */}
							<Fyllknapp
								mini
								onClick={null}
								disabled={true}
								title="Denne funksjonaliteten er foreløpig ikke tilgjengelig"
							>
								Fyll felter automatisk
							</Fyllknapp>
							<Hjelpetekst hjelpetekstFor="Fyllknapp">
								Når du har fylt ut perioden du ønsker å opprette A-meldinger for, vil det genereres
								et skjema for hver måned. Du kan velge om du ønsker å fylle ut alt selv, eller fylle
								feltene automatisk. Ved automatisk utfylling vil det bli generert en logisk
								historikk for A-meldingene i perioden. OBS! Denne funksjonaliteten er foreløpig ikke
								tilgjengelig, men vi jobber med saken.
							</Hjelpetekst>
						</div>
					</div>
					{periode.length > 0 && (
						<>
							<KjedeContainer>
								<DollyKjede
									objectList={periode}
									itemLimit={10}
									selectedIndex={selectedIndex}
									setSelectedIndex={setSelectedIndex}
									isLocked={erLenket}
								/>
								<KjedeIcon locked={erLenket} onClick={erLenket ? setErIkkeLenket : setErLenket} />
								<Hjelpetekst hjelpetekstFor="DollyKjede">
									Når du ser et lenke-symbol til høyre for månedsoversikten er alle måneder lenket
									sammen. Det vil si at om du gjør endringer på én måned vil disse bli gjort på alle
									månedene. Om du trykker på lenken vises en brutt lenke og månedene vil være
									uavhengige av hverandre. Ihvertfall nesten - endringer som gjøres på én måned vil
									kun påvirke den valgte måneden og månedene som kommer etter den i perioden.
								</Hjelpetekst>
							</KjedeContainer>
							{arbeidsforholdstype === 'frilanserOppdragstakerHonorarPersonerMm' &&
								periode.length > 1 && (
									<Slettknapp kind="trashcan" onClick={handleFjernMaaned}>
										Fjern måned
									</Slettknapp>
								)}
							<FormikDollyFieldArray
								name={`aareg[0].amelding[${selectedIndex}].arbeidsforhold`}
								header="Arbeidsforhold"
								newEntry={
									arbeidsforholdstype === 'forenkletOppgjoersordning'
										? initialForenkletOppgjoersordningOrg
										: initialArbeidsforholdOrg
								}
								canBeEmpty={false}
								handleNewEntry={handleNewEntry}
								handleRemoveEntry={handleRemoveEntry}
							>
								{(path: string, idx: number) => (
									<ArbeidsforholdForm
										path={path}
										key={idx}
										ameldingIndex={selectedIndex}
										arbeidsforholdIndex={idx}
										formikBag={formikBag}
										arbeidsgiverType={'EGEN'}
										erLenket={erLenket}
									/>
								)}
							</FormikDollyFieldArray>
						</>
					)}
				</>
			</div>
		</>
	)
}
