import React, { useState } from 'react'
import styled from 'styled-components'
import useBoolean from '@/utils/hooks/useBoolean'
import _ from 'lodash'
import { add, eachMonthOfInterval, format, isAfter, isDate } from 'date-fns'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { ArbeidKodeverk } from '@/config/kodeverk'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import Button from '@/components/ui/button/Button'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialArbeidsforholdOrg,
	initialFartoy,
	initialForenkletOppgjoersordningOrg,
} from '../initialValues'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'
import DollyKjede from '@/components/dollyKjede/DollyKjede'
import KjedeIcon from '@/components/dollyKjede/KjedeIcon'
import { Amelding, KodeverkValue } from '@/components/fagsystem/aareg/AaregTypes'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { fixTimezone } from '@/components/ui/form/formUtils'
import { useFormContext } from 'react-hook-form'

interface AmeldingFormProps {
	warningMessage?: any
}

export const KjedeContainer = styled.div`
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

export const AmeldingForm = ({ warningMessage }: AmeldingFormProps): JSX.Element => {
	const formMethods = useFormContext()
	const paths = {
		arbeidsforholdstype: 'aareg[0].arbeidsforholdstype',
		periode: 'aareg[0].genererPeriode.periode',
		amelding: 'aareg[0].amelding',
	}

	const arbeidsforholdstype = formMethods.watch(paths.arbeidsforholdstype)

	const fom = formMethods.watch('aareg[0].genererPeriode.fom')
	const fomDate = isDate(fom) ? fixTimezone(fom) : fom
	const tom = formMethods.watch('aareg[0].genererPeriode.tom')
	const tomDate = isDate(tom) ? fixTimezone(tom) : tom
	const periode = formMethods.watch(paths.periode)
	const ameldinger = formMethods.watch(paths.amelding)

	const [erLenket, setErLenket, setErIkkeLenket] = useBoolean(true)
	const [selectedIndex, setSelectedIndex] = useState(0)

	const handlePeriodeChange = (dato: string, type: string) => {
		const fixedDato = fixTimezone(dato)
		formMethods.setValue(`aareg[0].genererPeriode.${type}`, fixedDato, { shouldTouch: true })

		if ((type === 'tom' && fom) || (type === 'fom' && tom)) {
			const maanederPrev: Array<Amelding> = formMethods.watch(paths.amelding)
			const maaneder: Array<string> = []
			const startDate = dato && new Date(type === 'fom' ? dato : fomDate)
			const endDate = dato && new Date(type === 'tom' ? dato : tomDate)
			const maanederTmp = isAfter(endDate, startDate)
				? eachMonthOfInterval({
						start: startDate,
						end: endDate,
					})
				: []
			maanederTmp.forEach((maaned) => {
				maaneder.push(format(maaned, 'yyyy-MM'))
			})
			formMethods.setValue(paths.periode, maaneder)

			if (maaneder.length < maanederPrev.length) {
				const maanederFiltered = maanederPrev.filter((maaned) => maaneder.includes(maaned.maaned))
				formMethods.setValue(paths.amelding, maanederFiltered)
			} else {
				maaneder.forEach((mnd, idx) => {
					const currMaaned = formMethods
						.watch(paths.amelding)
						.find((element: Amelding) => element.maaned === mnd)
					formMethods.setValue(`${paths.amelding}[${idx}]`, {
						maaned: mnd,
						arbeidsforhold: currMaaned
							? currMaaned.arbeidsforhold
							: arbeidsforholdstype === 'forenkletOppgjoersordning'
								? [initialForenkletOppgjoersordningOrg]
								: [initialArbeidsforholdOrg],
					})
					if (arbeidsforholdstype === 'maritimtArbeidsforhold') {
						formMethods.setValue(
							`${paths.amelding}[${idx}].arbeidsforhold[0].fartoy`,
							initialFartoy,
						)
					}
				})
			}
		}
		formMethods.trigger('aareg')
	}

	const handleArbeidsforholdstypeChange = (event: KodeverkValue) => {
		const amelding = formMethods.watch(paths.amelding)
		const ameldingClone = _.cloneDeep(amelding)

		if (event.value === 'forenkletOppgjoersordning') {
			ameldingClone.forEach((_maaned: string, idx: number) => {
				_.set(ameldingClone[idx], 'arbeidsforhold', [initialForenkletOppgjoersordningOrg])
			})
		} else {
			ameldingClone.forEach((maaned: any, idx: number) => {
				if (arbeidsforholdstype === 'forenkletOppgjoersordning' || arbeidsforholdstype === '') {
					_.set(ameldingClone[idx], 'arbeidsforhold', [initialArbeidsforholdOrg])
				}
				if (event.value === 'maritimtArbeidsforhold') {
					maaned.arbeidsforhold.forEach((_arbforh: Object, id: number) => {
						_.set(ameldingClone[idx], `arbeidsforhold[${id}].fartoy`, initialFartoy)
					})
				} else {
					maaned.arbeidsforhold.forEach((_arbforh: Object, id: number) => {
						_.set(ameldingClone[idx], `arbeidsforhold[${id}].fartoy`, undefined)
					})
				}
			})
		}
		formMethods.setValue(paths.amelding, ameldingClone)
		formMethods.setValue(paths.arbeidsforholdstype, event.value)
		formMethods.trigger('aareg')
	}

	const handleNewEntry = () => {
		ameldinger.forEach((_maaned: Amelding, idMaaned: number) => {
			if (!erLenket && idMaaned !== selectedIndex) {
				return
			}
			const currArbeidsforhold = _.get(
				formMethods.getValues(),
				`${paths.amelding}[${idMaaned}].arbeidsforhold`,
			)
			const nyttArbeidsforhold =
				arbeidsforholdstype === 'forenkletOppgjoersordning'
					? initialForenkletOppgjoersordningOrg
					: arbeidsforholdstype === 'maritimtArbeidsforhold'
						? { ...initialArbeidsforholdOrg, fartoy: initialFartoy }
						: initialArbeidsforholdOrg
			formMethods.setValue(`${paths.amelding}[${idMaaned}].arbeidsforhold`, [
				...currArbeidsforhold,
				nyttArbeidsforhold,
			])
		})
		formMethods.trigger(paths.amelding)
	}

	const handleRemoveEntry = (idArbeidsforhold: number) => {
		ameldinger.forEach((_maaned: Amelding, idMaaned: number) => {
			if (!erLenket && idMaaned !== selectedIndex) {
				return
			}
			const currArbeidsforhold = _.get(
				formMethods.getValues(),
				`${paths.amelding}[${idMaaned}].arbeidsforhold`,
			)
			currArbeidsforhold?.splice(idArbeidsforhold, 1)
			formMethods.setValue(`${paths.amelding}[${idMaaned}].arbeidsforhold`, currArbeidsforhold)
			formMethods.trigger(paths.amelding)
		})
	}

	const handleFjernMaaned = () => {
		const currAmelding = formMethods.watch(paths.amelding)
		currAmelding.splice(selectedIndex, 1)
		formMethods.setValue(paths.amelding, currAmelding)

		const nyPeriode = periode
		nyPeriode.splice(selectedIndex, 1)
		formMethods.setValue(paths.periode, nyPeriode)

		if (periode?.length === 1) {
			setSelectedIndex(0)
		} else if (selectedIndex > 0) {
			setSelectedIndex(selectedIndex - 1)
		} else {
			setSelectedIndex(selectedIndex)
		}
		formMethods.trigger(paths.amelding)
	}

	return (
		<>
			<div className="flexbox--align-center">
				<h3>A-melding</h3>
				<Hjelpetekst>
					Om du har opprettet dine egne organisasjoner kan du sende A-meldinger til disse. Velg
					først hvilken type arbeidsforhold du ønsker å opprette, deretter kan du fylle ut resten av
					informasjonen.
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
					value={arbeidsforholdstype}
				/>
				<>
					<Monthpicker
						name="aareg[0].genererPeriode.fom"
						label="F.o.m. kalendermåned"
						date={fom}
						handleDateChange={(dato: string) => handlePeriodeChange(dato, 'fom')}
						minDate={add(new Date(), { years: -25 })}
						maxDate={add(new Date(), { years: 1 })}
					/>
					<Monthpicker
						name="aareg[0].genererPeriode.tom"
						label="T.o.m. kalendermåned"
						date={tom}
						handleDateChange={(dato: string) => handlePeriodeChange(dato, 'tom')}
						minDate={add(new Date(), { years: -25 })}
						maxDate={add(new Date(), { years: 25 })}
					/>
					<div className="flexbox--full-width">
						<div className="flexbox--flex-wrap">
							{/* //TODO lag onClick for å fylle felter */}
							{/* <Fyllknapp onClick={null} disabled={!fom || !tom}> */}
							<Fyllknapp
								onClick={null}
								disabled={true}
								title="Denne funksjonaliteten er foreløpig ikke tilgjengelig"
							>
								Fyll felter automatisk
							</Fyllknapp>
							<Hjelpetekst>
								Når du har fylt ut perioden du ønsker å opprette A-meldinger for, vil det genereres
								et skjema for hver måned. Du kan velge om du ønsker å fylle ut alt selv, eller fylle
								feltene automatisk. Ved automatisk utfylling vil det bli generert en logisk
								historikk for A-meldingene i perioden. OBS! Denne funksjonaliteten er foreløpig ikke
								tilgjengelig, men vi jobber med saken.
							</Hjelpetekst>
						</div>
					</div>
					{periode?.length > 0 && (
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
								<Hjelpetekst>
									Når du ser et lenke-symbol til høyre for månedsoversikten er alle måneder lenket
									sammen. Det vil si at om du gjør endringer på én måned vil disse bli gjort på alle
									månedene. Om du trykker på lenken vises en brutt lenke og månedene vil være
									uavhengige av hverandre. Ihvertfall nesten - endringer som gjøres på én måned vil
									kun påvirke den valgte måneden og månedene som kommer etter den i perioden.
								</Hjelpetekst>
							</KjedeContainer>
							{arbeidsforholdstype === 'frilanserOppdragstakerHonorarPersonerMm' &&
								periode?.length > 1 && (
									<Slettknapp kind="trashcan" onClick={handleFjernMaaned}>
										Fjern måned
									</Slettknapp>
								)}
							<FormDollyFieldArray
								name={`${paths.amelding}[${selectedIndex}].arbeidsforhold`}
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
										arbeidsgiverType={'EGEN'}
										erLenket={erLenket}
										warningMessage={warningMessage}
									/>
								)}
							</FormDollyFieldArray>
						</>
					)}
				</>
			</div>
		</>
	)
}
