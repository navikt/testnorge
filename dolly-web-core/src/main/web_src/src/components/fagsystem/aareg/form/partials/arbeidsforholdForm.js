import React from 'react'
import { FieldArray } from 'formik'
// import { subYears } from 'date-fns'
import _get from 'lodash/get'
import { FieldArrayAddButton, FieldArrayRemoveButton } from '~/components/ui/form/formUtils'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
// import Formatters from '~/utils/DataFormatter'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { TimeloennetForm } from './timeloennetForm'
import { PermisjonForm } from './permisjonForm'
import { UtenlandsoppholdForm } from './utenlandsoppholdForm'

export const ArbeidsforholdForm = ({ formikBag, initial }) => {
	const initialTimeloennet = initial.antallTimerForTimeloennet[0]

	const initialPermisjon = {
		permisjonsPeriode: {
			fom: '',
			tom: ''
		},
		permisjonsprosent: '',
		permisjonOgPermittering: ''
	}

	const initialUtenlandsopphold = {
		periode: {
			fom: '',
			tom: ''
		},
		land: ''
	}

	const arbeidsforholdArray = _get(formikBag.values, 'aareg', [])

	const fjern = (idx, path, currentArray) => {
		const nyArray = currentArray.filter((_, _idx) => _idx !== idx)
		formikBag.setFieldValue(path, nyArray)
	}

	const leggTil = (name, initial) =>
		formikBag.setFieldValue(name, _get(formikBag.values, name).concat([initial]))

	// const leggTilTimeloennet = name =>
	// 	formikBag.setFieldValue(name, _get(formikBag.values, name).concat([initialTimeloennet]))

	// const leggTilPermisjon = name =>
	// 	formikBag.setFieldValue(name, _get(formikBag.values, name).concat([initialPermisjon]))

	return (
		<FieldArray name="aareg">
			{({ push }) => (
				<div>
					{/* {<h4>Arbeidsforhold</h4>} */}
					{arbeidsforholdArray.map((arbeidsforhold, idx) => (
						<React.Fragment key={idx}>
							{/* {console.log('arbeidsforhold :', arbeidsforhold)} */}
							{/* <div className="flexbox"> */}

							<FormikDatepicker name={`aareg[${idx}].ansettelsesPeriode.fom`} label="Ansatt fra" />
							<FormikDatepicker name={`aareg[${idx}].ansettelsesPeriode.tom`} label="Ansatt til" />
							{/* Skal vise value (kode) i tillegg */}
							<FormikSelect
								name={`aareg[${idx}].arbeidsavtale.yrke`}
								label="Yrke"
								kodeverk="Yrker"
								size="xxlarge"
							/>
							{/* Skal være number +- ting */}
							<FormikTextInput
								name={`aareg[${idx}].arbeidsavtale.stillingsprosent`}
								label="Stillingsprosent"
							/>
							<FormikDatepicker
								name={`aareg[${idx}].arbeidsavtale.endringsdatoStillingsprosent`}
								label="Endringsdato stillingsprosent"
							/>
							<FormikSelect
								name={`aareg[${idx}].arbeidsgiver.aktoertype`}
								label="Type arbeidsgiver"
								options={Options('aktoertype')}
								size="medium"
								// onChange={() => formikBag.setFieldValue(arbeidsforhold.arbeidsgiver.aktoerId, '')}
								// bør på en eller annen måte nullstille aktørtype når den endres!
							/>
							{arbeidsforhold.arbeidsgiver.aktoertype === 'ORG' && (
								<FormikSelect // evt. felt man kan skrive i også?
									name={`aareg[${idx}].arbeidsgiver.aktoerId`} //ident?
									label="Arbeidsgiver orgnummer"
									options={Options('orgnummer')}
								/>
							)}
							{arbeidsforhold.arbeidsgiver.aktoertype === 'PERS' && (
								<FormikTextInput
									name={`aareg[${idx}].arbeidsgiver.aktoerId`} //ident?
									label="Arbeidsgiver ident"
								/>
							)}
							<FormikSelect
								name={`aareg[${idx}].arbeidsforholdstype`}
								label="Arbeidsforholdstype"
								kodeverk="Arbeidsforholdstyper"
								size="xxlarge"
							/>
							{/* Startdato??? */}
							{/* Sluttdato??? */}
							<FormikSelect
								name={`aareg[${idx}].arbeidsavtale.arbeidstidsordning`}
								label="Arbeidstidsordning"
								kodeverk="Arbeidstidsordninger"
								size="xxlarge"
							/>
							{/* Skal være number +- ting */}
							<FormikTextInput
								name={`aareg[${idx}].arbeidsavtale.antallKonverterteTimer`}
								label="Antall konverterte timer"
							/>
							{/* <FormikSelect
								name={`aareg[${idx}].arbeidsavtale.avloenningstype`}
								label="Avlønningstype"
								kodeverk="Avlønningstyper"
								size="medium"
							/> */}
							{/* <FormikDatepicker
								name={`aareg[${idx}].arbeidsavtale.sisteLoennsendringsdato`}
								label="Siste lønnsendringsdato"
							/> */}
							<FormikTextInput
								name={`aareg[${idx}].arbeidsavtale.avtaltArbeidstimerPerUke`}
								label="Avtalte timer per uke"
							/>
							{/* {arbeidsforhold.arbeidsavtale.avloenningstype === 'time' && ( */}
							{/* Egentlig bare hvis avloenningstype er time, men avlønningstype skal ikke finnes lenger */}
							<React.Fragment>
								<TimeloennetForm
									name={`aareg[${idx}].antallTimerForTimeloennet`}
									formikBag={formikBag}
									fjern={fjern}
								/>
								<FieldArrayAddButton
									title="Legg til periode"
									onClick={() =>
										leggTil(`aareg[${idx}].antallTimerForTimeloennet`, initialTimeloennet)
									}
								/>
							</React.Fragment>
							{/* )} */}
							{arbeidsforhold.permisjon && (
								<PermisjonForm
									name={`aareg[${idx}].permisjon`}
									formikBag={formikBag}
									fjern={fjern}
								/>
							)}
							<FieldArrayAddButton
								title="Legg til permisjon"
								onClick={() => leggTil(`aareg[${idx}].permisjon`, initialPermisjon)}
							/>
							{arbeidsforhold.utenlandsopphold && (
								<UtenlandsoppholdForm
									name={`aareg[${idx}].utenlandsopphold`}
									formikBag={formikBag}
									fjern={fjern}
								/>
							)}
							<FieldArrayAddButton
								title="Legg til utenlandsopphold"
								onClick={() => leggTil(`aareg[${idx}].utenlandsopphold`, initialUtenlandsopphold)}
							/>
							<FieldArrayRemoveButton onClick={() => fjern(idx, 'aareg', arbeidsforholdArray)} />
							{/* </div> */}
						</React.Fragment>
					))}
					<FieldArrayAddButton title="Legg til arbeidsforhold" onClick={() => push(initial)} />
					{/* TODO legg til fjern */}
				</div>
			)}
		</FieldArray>
	)
}
