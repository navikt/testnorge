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
	startDato: new Date(),
	orgnummer: '',
	arbeidsforholdId: ''
}

const initialValuesSykemelding = {
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

export const SykemeldingForm = ({ formikBag }: SykemeldingForm) => {
	const [typeSykemelding, setTypeSykemelding] = useState(
		formikBag.values.sykdom.sykemelding.hasOwnProperty('hovedDiagnose')
			? 'detaljertSykemelding'
			: 'syntSykemelding'
	)

	const handleToggleChange = event => {
		setTypeSykemelding(event.target.value)
		if (event.target.value === 'detaljertSykemelding') {
			formikBag.setFieldValue('sykdom.sykemelding', initialValuesSykemelding)
		} else formikBag.setFieldValue('sykdom.sykemelding', initialValuesSyntSykemelding)
	}

	const handleDiagnoseChange = (v, path) => {
		console.log('v :>> ', v)
		console.log('path :>> ', path)
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
			<ToggleGruppe onChange={handleToggleChange} name="sykdom.sykemelding">
				{toggleValues.map(val => (
					<ToggleKnapp key={val.value} value={val.value} checked={typeSykemelding === val.value}>
						{val.label}
					</ToggleKnapp>
				))}
			</ToggleGruppe>
			{typeSykemelding === 'syntSykemelding' && (
				<div className="flexbox--flex-wrap">
					<FormikDatepicker name="sykdom.sykemelding.startDato" label="Startdato" />
					<OrganisasjonMedArbeidsforholdSelect
						path="sykdom.sykemelding.orgnummer"
						label="Organisasjonsnummer"
					/>
					<FormikTextInput
						name="sykdom.sykemelding.arbeidsforholdId"
						label="Arbeidsforhold-ID"
						type="number"
					/>
				</div>
			)}
			{typeSykemelding === 'detaljertSykemelding' && (
				<div className="flexbox--wrap">
					<div className="flexbox--flex-wrap">
						<FormikDatepicker name="sykdom.sykemelding.startDato" label="Startdato" />
						<FormikCheckbox
							name="sykdom.sykemelding.umiddelbarBistand"
							label="Trenger umiddelbar bistand"
							size="medium"
							checkboxMargin
						/>
						<FormikCheckbox
							name="sykdom.sykemelding.manglendeTilretteleggingPaaArbeidsplassen"
							label="Manglende tilrettelegging på arbeidsplassen"
							size="large"
							checkboxMargin
						/>
					</div>
					<Kategori title="Diagnose" vis="sykdom.sykemelding">
						<div className="flexbox--flex-wrap">
							<FormikSelect
								name="sykdom.sykemelding.hovedDiagnose.diagnosekode"
								label="Diagnose"
								options={Options('diagnose')}
								afterChange={v => handleDiagnoseChange(v, 'sykdom.sykemelding.hovedDiagnose')}
								size="xlarge"
								isClearable={false}
							/>
							<FormikTextInput name="sykdom.sykemelding.hovedDiagnose.system" label="System" />
						</div>
					</Kategori>
					<FormikDollyFieldArray
						name="sykdom.sykemelding.biDiagnoser"
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
					<Kategori title="Lege" vis="sykdom.sykemelding">
						<FormikSelect
							name="sykdom.sykemelding.lege"
							label="Lege"
							// options={legeOptions}
							size="xlarge"
							isClearable={false}
						/>
					</Kategori>
					<Kategori title="Arbeidsgiver" vis="sykdom.sykemelding">
						<FormikTextInput
							name="sykdom.sykemelding.arbeidsgiver.navn"
							label="Navn"
							size="large"
						/>
						<FormikTextInput
							name="sykdom.sykemelding.arbeidsgiver.yrkesbetegnelse"
							label="Yrkesbetegnelse"
							size="large"
						/>
						<FormikTextInput
							name="sykdom.sykemelding.arbeidsgiver.stillingsprosent"
							label="Stillingsprosent"
							type="number"
						/>
					</Kategori>
					<FormikDollyFieldArray
						name="sykdom.sykemelding.perioder"
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
					<Kategori title="Detaljer" vis="sykdom.sykemelding">
						<FormikTextInput
							name="sykdom.sykemelding.detaljer.tiltakNav"
							label="Tiltak fra Nav"
							size="large"
						/>
						<FormikTextInput
							name="sykdom.sykemelding.detaljer.tiltakArbeidsplass"
							label="Tiltak på arbeidsplass"
							size="large"
						/>
						<FormikTextInput
							name="sykdom.sykemelding.detaljer.beskrivHensynArbeidsplassen"
							label="Hensyn på arbeidsplass"
							size="large"
						/>
						<FormikCheckbox
							name="sykdom.sykemelding.detaljer.arbeidsforEtterEndtPeriode"
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
