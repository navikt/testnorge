import React, { useState } from 'react'
import { get as _get } from 'lodash'
import { FormikProps } from 'formik'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

interface SykemeldingForm {
	formikBag: FormikProps<{}>
}

const initialValuesDiagnose = {
	diagnose: '',
	diagnosekode: '',
	system: ''
}

const initialValuesPeriode = {
	aktivitet: {
		aktivitet: '',
		behandlingsdager: 0,
		grad: 0,
		reisetilskudd: false
	},
	fom: '',
	tom: ''
}

const initialValuesSyntSykemelding = {
	syntSykemelding: {
		startDato: new Date(),
		orgnummer: '',
		arbeidsforholdId: ''
	}
}

const initialValuesDetaljertSykemelding = {
	detaljertSykemelding: {
		arbeidsgiver: {
			navn: '',
			stillingsprosent: 100,
			yrkesbetegnelse: ''
		},
		biDiagnoser: [],
		detaljer: {
			arbeidsforEtterEndtPeriode: false,
			beskrivHensynArbeidsplassen: '',
			tiltakArbeidsplass: '',
			tiltakNav: ''
		},
		hovedDiagnose: initialValuesDiagnose,
		lege: {
			etternavn: '',
			fornavn: '',
			hprId: '',
			ident: '',
			mellomnavn: ''
		},
		manglendeTilretteleggingPaaArbeidsplassen: false,
		// mottaker: backend?
		// pasient: backend
		perioder: [initialValuesPeriode],
		// sender: backend?
		startDato: new Date(),
		umiddelbarBistand: false
	}
}

export const SykemeldingForm = ({ formikBag }: SykemeldingForm) => {
	const [typeSykemelding, setTypeSykemelding] = useState(
		formikBag.values.sykemelding.hasOwnProperty('detaljertSykemelding')
			? 'detaljertSykemelding'
			: 'syntSykemelding'
	)

	const handleToggleChange = event => {
		setTypeSykemelding(event.target.value)
		if (event.target.value === 'detaljertSykemelding') {
			formikBag.setFieldValue('sykemelding', initialValuesDetaljertSykemelding)
		} else formikBag.setFieldValue('sykemelding', initialValuesSyntSykemelding)
	}

	const handleDiagnoseChange = (v, path) => {
		formikBag.setFieldValue(`${path}.diagnose`, v.label)
	}

	const toggleValues = [
		{
			value: 'syntSykemelding',
			label: 'Generer sykemelding automatisk'
		},
		{
			value: 'detaljertSykemelding',
			label: 'Lag detaljert sykemelding'
		}
	]

	// const legeOptions = SelectOptionsOppslag.hentLeger()

	return (
		<div className="toggle--wrapper">
			<ToggleGruppe onChange={handleToggleChange} name="sykemelding">
				{toggleValues.map(val => (
					<ToggleKnapp key={val.value} value={val.value} checked={typeSykemelding === val.value}>
						{val.label}
					</ToggleKnapp>
				))}
			</ToggleGruppe>
			{typeSykemelding === 'syntSykemelding' && (
				<div className="flexbox--flex-wrap">
					<FormikDatepicker name="sykemelding.syntSykemelding.startDato" label="Startdato" />
					<OrganisasjonMedArbeidsforholdSelect
						path="sykemelding.syntSykemelding.orgnummer"
						label="Organisasjonsnummer"
					/>
					<FormikTextInput
						name="sykemelding.syntSykemelding.arbeidsforholdId"
						label="Arbeidsforhold-ID"
						type="number"
					/>
				</div>
			)}
			{typeSykemelding === 'detaljertSykemelding' && (
				<div className="flexbox--wrap">
					<div className="flexbox--flex-wrap">
						<FormikDatepicker name="sykemelding.detaljertSykemelding.startDato" label="Startdato" />
						<FormikCheckbox
							name="sykemelding.detaljertSykemelding.umiddelbarBistand"
							label="Trenger umiddelbar bistand"
							size="medium"
							checkboxMargin
						/>
						<FormikCheckbox
							name="sykemelding.detaljertSykemelding.manglendeTilretteleggingPaaArbeidsplassen"
							label="Manglende tilrettelegging på arbeidsplassen"
							size="large"
							checkboxMargin
						/>
					</div>
					<Kategori title="Diagnose" vis="sykemelding">
						<div className="flexbox--flex-wrap">
							<FormikSelect
								name="sykemelding.detaljertSykemelding.hovedDiagnose.diagnosekode"
								label="Diagnose"
								options={Options('diagnose')}
								afterChange={v =>
									handleDiagnoseChange(v, 'sykemelding.detaljertSykemelding.hovedDiagnose')
								}
								size="xlarge"
								isClearable={false}
							/>
							<FormikTextInput
								name="sykemelding.detaljertSykemelding.hovedDiagnose.system"
								label="System"
							/>
						</div>
					</Kategori>
					<FormikDollyFieldArray
						name="sykemelding.detaljertSykemelding.biDiagnoser"
						header="Bidiagnose"
						newEntry={initialValuesDiagnose}
					>
						{(path: string) => (
							<>
								<FormikSelect
									name={`${path}.diagnosekode`}
									label="Diagnose"
									options={Options('diagnose')}
									afterChange={v => handleDiagnoseChange(v, path)}
									size="xlarge"
									isClearable={false}
								/>
								<FormikTextInput name={`${path}.system`} label="System" />
							</>
						)}
					</FormikDollyFieldArray>
					<Kategori title="Lege" vis="sykemelding">
						<FormikSelect
							name="sykemelding.detaljertSykemelding.lege"
							label="Lege"
							size="xlarge"
							isClearable={false}
						/>
					</Kategori>
					<Kategori title="Arbeidsgiver" vis="sykemelding">
						<FormikTextInput
							name="sykemelding.detaljertSykemelding.arbeidsgiver.navn"
							label="Navn"
							size="large"
						/>
						<FormikTextInput
							name="sykemelding.detaljertSykemelding.arbeidsgiver.yrkesbetegnelse"
							label="Yrkesbetegnelse"
							size="large"
						/>
						<FormikTextInput
							name="sykemelding.detaljertSykemelding.arbeidsgiver.stillingsprosent"
							label="Stillingsprosent"
							type="number"
						/>
					</Kategori>
					<FormikDollyFieldArray
						name="sykemelding.detaljertSykemelding.perioder"
						header="Periode"
						newEntry={initialValuesPeriode}
					>
						{(path: string) => (
							<>
								<FormikDatepicker name={`${path}.fom`} label="F.o.m. dato" />
								<FormikDatepicker name={`${path}.tom`} label="T.o.m. dato" />
								<FormikSelect
									name={`${path}.aktivitet.aktivitet`}
									label="Aktivitet"
									options={Options('aktivitet')}
								/>
								<FormikTextInput
									name={`${path}.aktivitet.behandlingsdager`}
									label="Antall behandlingsdager"
									type="number"
									disabled={
										_get(formikBag.values, `${path}.aktivitet.aktivitet`) === '' ||
										_get(formikBag.values, `${path}.aktivitet.aktivitet`) === null
											? false
											: true
									}
									// TODO: Oppdateres for sent, og funker ikke motsatt vei - fiks den!
								/>
								<FormikTextInput name={`${path}.aktivitet.grad`} label="Grad" type="number" />
								<FormikCheckbox
									name={`${path}.aktivitet.reisetilskudd`}
									label="Har reisetilskudd"
									checkboxMargin
								/>
							</>
						)}
					</FormikDollyFieldArray>
					<Kategori title="Detaljer" vis="sykemelding">
						<FormikTextInput
							name="sykemelding.detaljertSykemelding.detaljer.tiltakNav"
							label="Tiltak fra Nav"
							size="large"
						/>
						<FormikTextInput
							name="sykemelding.detaljertSykemelding.detaljer.tiltakArbeidsplass"
							label="Tiltak på arbeidsplass"
							size="large"
						/>
						<FormikTextInput
							name="sykemelding.detaljertSykemelding.detaljer.beskrivHensynArbeidsplassen"
							label="Hensyn på arbeidsplass"
							size="large"
						/>
						<FormikCheckbox
							name="sykemelding.detaljertSykemelding.detaljer.arbeidsforEtterEndtPeriode"
							label="Arbeidsfør etter endt periode"
							size="medium"
							checkboxMargin
						/>
					</Kategori>
				</div>
			)}
		</div>
	)
}
