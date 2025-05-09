import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { SelectOptionsDiagnoser } from './SelectOptionsDiagnoser'
import { ArbeidKodeverk } from '@/config/kodeverk'
import {
	Arbeidsgiver,
	Helsepersonell,
	SykemeldingForm,
} from '@/components/fagsystem/sykdom/SykemeldingTypes'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import { getRandomValue } from '@/components/fagsystem/utils'
import { useContext, useEffect } from 'react'
import { useHelsepersonellOptions } from '@/utils/hooks/useSelectOptions'
import { useSykemeldingValidering } from '@/utils/hooks/useSykemelding'
import { DollyErrorMessageWrapper } from '@/utils/DollyErrorMessageWrapper'
import { useWatch } from 'react-hook-form'
import { SwrMutateContext } from '@/components/bestillingsveileder/SwrMutateContext'

type DiagnoseSelect = {
	diagnoseNavn: string
}

const initialValuesDiagnose = {
	diagnose: '',
	diagnosekode: '',
	system: '',
}

const initialValuesPeriode = {
	aktivitet: {
		aktivitet: null as unknown as string,
		behandlingsdager: 0,
		grad: 0,
		reisetilskudd: false,
	},
	fom: '',
	tom: '',
}

const KODESYSTEM = '2.16.578.1.12.4.1.1.7170'

export const DetaljertSykemelding = ({ formMethods }: SykemeldingForm) => {
	const setContextMutate: any = useContext(SwrMutateContext)

	const handleDiagnoseChange = (v: DiagnoseSelect, path: string) => {
		formMethods.setValue(`${path}.diagnose`, v.diagnoseNavn)
		formMethods.setValue(`${path}.system`, KODESYSTEM)
	}

	const detaljertSykemelding = useWatch({
		name: 'sykemelding.detaljertSykemelding',
		control: formMethods.control,
	})

	const { data, mutate, errorMessage } = useSykemeldingValidering(detaljertSykemelding)

	useEffect(() => {
		if (errorMessage && errorMessage.length > 0) {
			formMethods.setError('manual.sykemelding.detaljertSykemelding', {
				type: 'manual',
				message: errorMessage,
			})
		} else formMethods.clearErrors('manual.sykemelding.detaljertSykemelding')
	}, [errorMessage, data])

	useEffect(() => {
		setContextMutate(() => mutate)
		formMethods.setError('manual.sykemelding.detaljertSykemelding', {
			type: 'manual',
			message: 'Trykk "Videre" for å validere sykemeldingen',
		})
	}, [mutate])

	const handleLegeChange = (v: Helsepersonell) => {
		formMethods.setValue('sykemelding.detaljertSykemelding.helsepersonell', {
			etternavn: v.etternavn,
			fornavn: v.fornavn,
			hprId: v.hprId,
			ident: v.fnr,
			mellomnavn: v.mellomnavn,
			samhandlerType: v.samhandlerType,
		})
	}

	const handleArbeidsgiverChange = (v: Arbeidsgiver) => {
		formMethods.setValue('sykemelding.detaljertSykemelding.mottaker', {
			navn: v?.navn || null,
			orgNr: v?.orgnr || null,
			adresse: {
				by: v?.forretningsAdresse?.poststed || null,
				gate: v?.forretningsAdresse?.adresse || null,
				land: v?.forretningsAdresse?.landkode || null,
				postnummer: v?.forretningsAdresse?.postnr || null,
			},
		})
	}

	const { kodeverk: yrker } = useKodeverk(ArbeidKodeverk.Yrker)
	const randomYrke = getRandomValue(yrker)

	useEffect(() => {
		const yrkePath = 'sykemelding.detaljertSykemelding.arbeidsgiver.yrkesbetegnelse'
		if (formMethods.watch(yrkePath) === '') {
			formMethods.setValue(yrkePath, randomYrke?.value || '')
			formMethods.trigger(yrkePath)
		}
	}, [randomYrke])

	const { helsepersonellOptions, helsepersonellError } = useHelsepersonellOptions()
	const randomHelsepersonell = getRandomValue(helsepersonellOptions)

	useEffect(() => {
		if (
			formMethods.watch('sykemelding.detaljertSykemelding.helsepersonell.ident') === '' &&
			randomHelsepersonell
		) {
			handleLegeChange(randomHelsepersonell)
			formMethods.trigger('sykemelding.detaljertSykemelding.helsepersonell')
		}
	}, [randomHelsepersonell])

	return (
		<div className="flexbox--wrap">
			<DollyErrorMessageWrapper name={'manual.sykemelding.detaljertSykemelding'} />
			<div className="flexbox--flex-wrap">
				<FormDatepicker name="sykemelding.detaljertSykemelding.startDato" label="Startdato" />
				<FormCheckbox
					name="sykemelding.detaljertSykemelding.umiddelbarBistand"
					checkboxMargin
					label="Trenger umiddelbar bistand"
				/>
				<FormCheckbox
					name="sykemelding.detaljertSykemelding.manglendeTilretteleggingPaaArbeidsplassen"
					label="Manglende tilrettelegging på arbeidsplassen"
					checkboxMargin
				/>
			</div>
			<Kategori title="Diagnose" vis="sykemelding">
				<div className="flexbox--flex-wrap">
					<FormSelect
						name="sykemelding.detaljertSykemelding.hovedDiagnose.diagnosekode"
						label="Diagnose"
						options={SelectOptionsDiagnoser()}
						afterChange={(v: DiagnoseSelect) =>
							handleDiagnoseChange(v, 'sykemelding.detaljertSykemelding.hovedDiagnose')
						}
						size="xlarge"
						isClearable={false}
					/>
				</div>
			</Kategori>
			<FormDollyFieldArray
				name="sykemelding.detaljertSykemelding.biDiagnoser"
				header="Bidiagnose"
				newEntry={initialValuesDiagnose}
			>
				{(path: string) => (
					<FormSelect
						name={`${path}.diagnosekode`}
						label="Diagnose"
						options={SelectOptionsDiagnoser()}
						afterChange={(v: DiagnoseSelect) => handleDiagnoseChange(v, path)}
						size="xlarge"
						isClearable={false}
					/>
				)}
			</FormDollyFieldArray>
			<Kategori title="Tilbakedatering" vis="sykemelding">
				<div className="flexbox--flex-wrap">
					<FormTextInput
						name="sykemelding.detaljertSykemelding.kontaktMedPasient.begrunnelseIkkeKontakt"
						label="Begrunnelse ikke kontakt"
						size="xlarge"
					/>
					<FormDatepicker
						name={'sykemelding.detaljertSykemelding.kontaktMedPasient.kontaktDato'}
						label="Kontaktdato"
					/>
				</div>
			</Kategori>
			<Kategori title="Helsepersonell" vis="sykemelding">
				<FormSelect
					name="sykemelding.detaljertSykemelding.helsepersonell.ident"
					label="Helsepersonell"
					options={helsepersonellOptions}
					size="xxxlarge"
					afterChange={(v: Helsepersonell) => handleLegeChange(v)}
					isClearable={false}
					feil={helsepersonellError}
				/>
			</Kategori>
			<Kategori title="Arbeidsgiver" vis="sykemelding">
				<div className="flexbox--flex-wrap">
					<OrganisasjonMedArbeidsforholdSelect
						path="sykemelding.detaljertSykemelding.arbeidsgiver.navn"
						label="Navn"
						afterChange={(v: Arbeidsgiver) => handleArbeidsgiverChange(v)}
						valueNavn={true}
						isClearable={true}
					/>
					<FormSelect
						name="sykemelding.detaljertSykemelding.arbeidsgiver.yrkesbetegnelse"
						label="Yrkesbetegnelse"
						kodeverk={ArbeidKodeverk.Yrker}
						size="xxxlarge"
						isClearable={false}
						optionHeight={50}
					/>
					<FormTextInput
						name="sykemelding.detaljertSykemelding.arbeidsgiver.stillingsprosent"
						label="Stillingsprosent"
						type="number"
					/>
				</div>
			</Kategori>
			<FormDollyFieldArray
				name="sykemelding.detaljertSykemelding.perioder"
				header="Periode"
				newEntry={initialValuesPeriode}
			>
				{(path: string) => (
					<>
						<FormDatepicker name={`${path}.fom`} label="F.o.m. dato" />
						<FormDatepicker name={`${path}.tom`} label="T.o.m. dato" />
						<FormSelect
							name={`${path}.aktivitet.aktivitet`}
							label="Aktivitet"
							options={Options('aktivitet')}
						/>
						<FormTextInput
							name={`${path}.aktivitet.behandlingsdager`}
							label="Antall behandlingsdager"
							type="number"
						/>
						<FormTextInput name={`${path}.aktivitet.grad`} label="Grad" type="number" />
						<FormCheckbox
							name={`${path}.aktivitet.reisetilskudd`}
							id={`${path}.aktivitet.reisetilskudd`}
							label="Har reisetilskudd"
							checkboxMargin
						/>
					</>
				)}
			</FormDollyFieldArray>
			<Kategori title="Detaljer" vis="sykemelding">
				<div className="flexbox--flex-wrap">
					<FormTextInput
						name="sykemelding.detaljertSykemelding.detaljer.tiltakNav"
						label="Tiltak fra Nav"
						size="xlarge"
					/>
					<FormTextInput
						name="sykemelding.detaljertSykemelding.detaljer.tiltakArbeidsplass"
						label="Tiltak på arbeidsplass"
						size="xlarge"
					/>
					<FormTextInput
						name="sykemelding.detaljertSykemelding.detaljer.beskrivHensynArbeidsplassen"
						label="Hensyn på arbeidsplass"
						size="xlarge"
					/>
					<FormCheckbox
						name="sykemelding.detaljertSykemelding.detaljer.arbeidsforEtterEndtPeriode"
						label="Arbeidsfør etter endt periode"
						size="small"
						checkboxMargin
					/>
				</div>
			</Kategori>
		</div>
	)
}
