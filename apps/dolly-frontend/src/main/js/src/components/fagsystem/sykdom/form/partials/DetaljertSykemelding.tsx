import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { SelectOptionsDiagnoser } from './SelectOptionsDiagnoser'
import { ArbeidKodeverk } from '@/config/kodeverk'
import {
	Arbeidsgiver,
	Helsepersonell,
	SykemeldingForm,
} from '@/components/fagsystem/sykdom/SykemeldingTypes'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import { getRandomValue } from '@/components/fagsystem/utils'
import React, { BaseSyntheticEvent, useContext, useEffect, useRef, useState } from 'react'
import { useHelsepersonellOptions } from '@/utils/hooks/useSelectOptions'
import { useSykemeldingValidering } from '@/utils/hooks/useSykemelding'
import { DollyErrorMessageWrapper } from '@/utils/DollyErrorMessageWrapper'
import { useWatch } from 'react-hook-form'
import { SwrMutateContext } from '@/components/bestillingsveileder/SwrMutateContext'
import { ArbeidsforholdToggle } from '@/components/shared/ArbeidsforholdToggle'
import { useOrganisasjonForvalter } from '@/utils/hooks/useDollyOrganisasjoner'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'

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

const initialValuesMottaker = {
	navn: '',
	orgNr: '',
	adresse: {
		by: '',
		gate: '',
		land: '',
		postnummer: '',
	},
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

	const [selectedOrgnummer, setSelectedOrgnummer] = useState<string | undefined>(undefined)
	const { organisasjoner, loading, hasBeenCalled } = useOrganisasjonForvalter([selectedOrgnummer])
	const lastAppliedOrg = useRef<string | undefined>(undefined)

	const mapForvalterOrganisasjon = (org: any) => {
		if (!org) {
			formMethods.setError('manual.sykemelding.detaljertSykemelding', {
				message: 'Ingen organisasjon funnet',
			})
			return initialValuesMottaker
		}

		const base = org.organisasjon || org

		if (Array.isArray(base.adresser)) {
			const valgt =
				base.adresser.find((a: any) => a.adressetype === 'FADR') ||
				base.adresser.find((a: any) => a.adressetype === 'PADR') ||
				base.adresser[0]

			const gate =
				(Array.isArray(valgt?.adresselinjer) && valgt.adresselinjer.filter(Boolean).join(', ')) ||
				null

			return {
				navn: base.navn || base.organisasjonNavn || base.organisasjonsnavn || null,
				orgNr:
					base.orgNr ||
					base.orgnr ||
					base.orgnummer ||
					base.organisasjonsnummer ||
					org.orgnr ||
					null,
				adresse: {
					by: valgt?.poststed || null,
					gate,
					land: valgt?.landkode || valgt?.land || null,
					postnummer: valgt?.postnr || valgt?.postnummer || null,
				},
			}
		}

		const fAddr =
			base.forretningsAdresse ||
			base.forretningsadresse ||
			base.adresse ||
			base.adresser ||
			base.postadresse ||
			{}

		const firstAdresse =
			(Array.isArray(fAddr)
				? fAddr[0]
				: fAddr.adresse ||
					(Array.isArray(fAddr.adresser) ? fAddr.adresser[0] : undefined) ||
					fAddr.gate ||
					fAddr.vegadresse) || null

		const gate =
			(Array.isArray(firstAdresse?.adresselinjer) &&
				firstAdresse.adresselinjer.filter(Boolean).join(', ')) ||
			(typeof firstAdresse === 'string' ? firstAdresse : null)

		return {
			navn: base.navn || base.organisasjonNavn || base.organisasjonsnavn || null,
			orgNr:
				base.orgNr || base.orgnr || base.orgnummer || base.organisasjonsnummer || org.orgnr || null,
			adresse: {
				by: fAddr.poststed || fAddr.postSted || fAddr.by || firstAdresse?.poststed || null,
				gate,
				land: fAddr.landkode || fAddr.land || firstAdresse?.landkode || null,
				postnummer: fAddr.postnr || fAddr.postnummer || firstAdresse?.postnr || null,
			},
		}
	}

	const setMottakerFromForvalter = (org: any) => {
		formMethods.setValue(
			'sykemelding.detaljertSykemelding.arbeidsgiver.navn',
			org?.navn || org?.organisasjonsnavn || null,
		)
		formMethods.setValue('sykemelding.detaljertSykemelding.mottaker', mapForvalterOrganisasjon(org))
	}

	const clearValgtOrganisasjon = () => {
		formMethods.clearErrors('manual.sykemelding.detaljertSykemelding.arbeidsgiver.navn')
		formMethods.clearErrors('manual.sykemelding.detaljertSykemelding.mottaker.orgNr')
		formMethods.clearErrors('sykemelding.detaljertSykemelding.arbeidsgiver.navn')
		formMethods.clearErrors('sykemelding.detaljertSykemelding.mottaker.orgNr')
		formMethods.clearErrors('sykemelding.detaljertSykemelding.mottaker.navn')
		formMethods.setValue('sykemelding.detaljertSykemelding.arbeidsgiver.navn', null)
		formMethods.setValue('sykemelding.detaljertSykemelding.mottaker', initialValuesMottaker)
	}

	const handleArbeidsgiverChange = (v: Arbeidsgiver | BaseSyntheticEvent) => {
		clearValgtOrganisasjon()
		if (v && 'target' in v) {
			const orgnummer = (v.target as HTMLInputElement)?.value?.trim()
			setSelectedOrgnummer(orgnummer || undefined)
			lastAppliedOrg.current = undefined
		} else {
			setSelectedOrgnummer((v as Arbeidsgiver)?.orgnr)
			lastAppliedOrg.current = undefined
		}
	}

	useEffect(() => {
		const org = organisasjoner?.[0]?.q1
		if (hasBeenCalled && !loading && !org) {
			formMethods.setError('manual.sykemelding.detaljertSykemelding.mottaker.orgNr', {
				message: 'Organisasjon ikke funnet i Q1',
			})
		} else {
			formMethods.clearErrors('manual.sykemelding.detaljertSykemelding.mottaker.orgNr')
		}
		const currentOrgNr = org?.organisasjonsnummer
		if (
			selectedOrgnummer &&
			currentOrgNr &&
			currentOrgNr === selectedOrgnummer &&
			lastAppliedOrg.current !== currentOrgNr
		) {
			setMottakerFromForvalter(org)
			lastAppliedOrg.current = currentOrgNr
		}
	}, [organisasjoner, selectedOrgnummer])

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
			<DollyErrorMessageWrapper name={['manual.sykemelding.detaljertSykemelding']} />
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
					<ArbeidsforholdToggle
						showMiljoeInfo={false}
						onToggle={() => {
							clearValgtOrganisasjon()
						}}
						path="sykemelding.detaljertSykemelding.organisasjon"
						afterChange={(v: any) => handleArbeidsgiverChange(v)}
						disablePrivat={true}
					/>
					{
						<div style={{ position: 'absolute', translate: '415px 70px' }}>
							{loading && <Loading label="Leter etter organisasjon" />}
							{!!formMethods.watch('sykemelding.detaljertSykemelding.mottaker.navn') && (
								<>
									<Icon kind="feedback-check-circle" style={{ marginRight: '5px' }} />
									<span style={{ position: 'relative', bottom: '5px' }}>
										Organisasjon funnet i Q1
									</span>
								</>
							)}
							<div style={{ width: 'max-content', position: 'absolute', translate: '0 -10px' }}>
								<DollyErrorMessageWrapper
									name={[
										'sykemelding.detaljertSykemelding.mottaker.orgNr',
										'manual.sykemelding.detaljertSykemelding.mottaker.orgNr',
										'sykemelding.detaljertSykemelding.arbeidsgiver.navn',
									]}
								/>
							</div>
						</div>
					}
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
