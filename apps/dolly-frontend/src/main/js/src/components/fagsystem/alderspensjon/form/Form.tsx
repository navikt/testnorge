import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import Panel from '@/components/ui/panel/Panel'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import React, { useContext, useEffect, useState } from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Alert, ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'
import * as _ from 'lodash-es'
import { add, getYear, isAfter, isDate, parseISO } from 'date-fns'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { validation } from '@/components/fagsystem/alderspensjon/form/validation'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'
import { getAlder } from '@/ducks/fagsystem'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import { genererTilfeldigeNavPersonidenter } from '@/utils/GenererTilfeldigeNavPersonidenter'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import {
	genInitialAlderspensjonSoknad,
	genInitialAlderspensjonVedtak,
} from '@/components/fagsystem/alderspensjon/form/initialValues'
import { useFormContext } from 'react-hook-form'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'

const StyledAlert = styled(Alert)`
	margin-bottom: 20px;

	&&& {
		.navds-alert__wrapper {
			max-width: 60rem;
		}
	}
`

export const alderspensjonPath = 'pensjonforvalter.alderspensjon'

export const AlderspensjonForm = () => {
	const formMethods = useFormContext()
	const { navEnheter } = useNavEnheter()

	const saksbehandler = formMethods.watch(`${alderspensjonPath}.saksbehandler`)
	const attesterer = formMethods.watch(`${alderspensjonPath}.attesterer`)

	const [randomAttesterere, setRandomAttesterere] = useState([])
	const [randomSaksbehandlere, setRandomSaksbehandlere] = useState([])

	useEffect(() => {
		setRandomAttesterere(genererTilfeldigeNavPersonidenter(attesterer))
		setRandomSaksbehandlere(genererTilfeldigeNavPersonidenter(saksbehandler))
	}, [])

	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const { nyBestilling, leggTil, importTestnorge, leggTilPaaGruppe } = opts?.is || {}

	function sjekkAlderFelt() {
		const harAlder =
			_.has(formMethods.getValues(), 'pdldata.opprettNyPerson.alder') &&
			_.has(formMethods.getValues(), 'pdldata.opprettNyPerson.foedtFoer')
		const alderNyPerson = formMethods.watch('pdldata.opprettNyPerson.alder')
		const foedtFoer =
			formMethods.watch('pdldata.opprettNyPerson.foedtFoer') &&
			new Date(formMethods.watch('pdldata.opprettNyPerson.foedtFoer'))
		const iverksettelsesdato =
			formMethods.watch('pensjonforvalter.alderspensjon.iverksettelsesdato') &&
			new Date(formMethods.watch('pensjonforvalter.alderspensjon.iverksettelsesdato'))
		const harGyldigAlder =
			(alderNyPerson && alderNyPerson > 61) ||
			(isDate(foedtFoer) && add(foedtFoer, { years: 62 }) < new Date()) ||
			(isDate(foedtFoer) && add(foedtFoer, { years: 62 }) < iverksettelsesdato)
		return { harAlder, alderNyPerson, foedtFoer, harGyldigAlder }
	}

	const { harAlder, alderNyPerson, foedtFoer, harGyldigAlder } = sjekkAlderFelt()

	const alderLeggTilPerson = getAlder(
		_.get(opts, 'personFoerLeggTil.pdl.hentPerson.foedselsdato[0].foedselsdato') ||
			_.get(opts, 'personFoerLeggTil.pdl.hentPerson.foedsel[0].foedselsdato'),
	)

	const alderImportertPerson = opts?.importPersoner?.map((person) =>
		getAlder(
			_.get(person, 'data.hentPerson.foedselsdato[0].foedselsdato') ||
				_.get(person, 'data.hentPerson.foedsel[0].foedselsdato'),
		),
	)

	const harUgyldigFoedselsaar = () => {
		let ugyldigFoedselsaar = false
		if (alderNyPerson && getYear(new Date()) - alderNyPerson < 1944) {
			ugyldigFoedselsaar = true
		} else if (foedtFoer && getYear(foedtFoer) < 1944) {
			ugyldigFoedselsaar = true
		} else if (leggTil) {
			if (
				opts?.personFoerLeggTil?.pdl?.foedsel?.some(
					(f) => f.foedselsaar < 1944 && !f.metadata?.historisk,
				)
			) {
				ugyldigFoedselsaar = true
			}
		} else if (importTestnorge) {
			const foedselListe = opts?.importPersoner?.flatMap(
				(person) => person?.data?.hentPerson?.foedsel,
			)
			if (foedselListe?.some((f) => f?.foedselsaar < 1944 && !f?.metadata?.historisk)) {
				ugyldigFoedselsaar = true
			}
		}
		const foedsel = _.get(formMethods.getValues(), 'pdldata.person.foedsel')
		if (foedsel) {
			const foedselsaar =
				foedsel[foedsel.length - 1]?.foedselsaar ||
				getYear(foedsel[foedsel.length - 1]?.foedselsdato)
			if (foedselsaar < 1944) {
				ugyldigFoedselsaar = true
			} else if (foedselsaar >= 1944) {
				ugyldigFoedselsaar = false
			}
		}
		return ugyldigFoedselsaar
	}

	const harNorskBankkonto =
		_.has(formMethods.getValues(), 'bankkonto.norskBankkonto') ||
		_.has(opts, 'personFoerLeggTil.kontoregister.aktivKonto')

	const harPopp =
		_.has(formMethods.getValues(), 'pensjonforvalter.inntekt') ||
		opts?.tidligereBestillinger?.some((bestilling) => bestilling.data?.pensjonforvalter?.inntekt)

	const gyldigSivilstand = ['GIFT', 'SAMBOER', 'REGISTRERT_PARTNER']

	const harPartner =
		_.get(formMethods.getValues(), 'pdldata.person.sivilstand')?.some((siv) =>
			gyldigSivilstand.includes(siv?.type),
		) ||
		_.get(opts, 'personFoerLeggTil.pdl.hentPerson.sivilstand')?.some((siv) =>
			gyldigSivilstand.includes(siv?.type),
		)

	const harPartnerImportertPerson = () => {
		const personerMedPartner = opts?.importPersoner?.filter((person) => {
			return person?.data?.hentPerson?.sivilstand?.some((siv) =>
				gyldigSivilstand.includes(siv?.type),
			)
		})
		return personerMedPartner?.length > 0
	}

	const adressetyper = {
		norge: 'NORGE',
		utland: 'UTLAND',
	}
	const valgtAdresseType = () => {
		const adresseUtenTilDato = _.get(
			formMethods.getValues(),
			'pdldata.person.bostedsadresse',
		)?.find((adresse) => adresse.gyldigFraOgMed && !adresse.gyldigTilOgMed)
		const gjeldendeAdresse =
			adresseUtenTilDato ||
			_.get(formMethods.getValues(), 'pdldata.person.bostedsadresse')?.reduce((prev, curr) => {
				if (
					!prev?.gyldigTilOgMed ||
					!curr?.gyldigTilOgMed ||
					curr?.gyldigTilOgMed?.isValid?.() === false ||
					prev?.gyldigTilOgMed?.isValid?.() === false
				)
					return null
				return isAfter(parseISO(prev.gyldigTilOgMed), parseISO(curr.gyldigTilOgMed)) ? prev : curr
			})
		return !gjeldendeAdresse || !gjeldendeAdresse?.adressetype
			? null
			: gjeldendeAdresse?.adressetype === 'UTENLANDSK_ADRESSE'
				? adressetyper.utland
				: adressetyper.norge
	}

	const harNorskAdresse = () => {
		if (opts?.personFoerLeggTil) {
			return (
				_.get(opts?.personFoerLeggTil, 'pdl.hentGeografiskTilknytning.gtType') !== 'UTLAND' ||
				valgtAdresseType() === adressetyper.norge
			)
		}

		if (opts?.importPersoner) {
			const personerMedNorskAdresse = opts?.importPersoner.filter((person) => {
				return person.data?.hentPerson?.bostedsadresse?.some(
					(adresse) => adresse.vegadresse && !adresse.metadata?.historisk,
				)
			})
			return personerMedNorskAdresse?.length > 0
		}

		return opts?.identtype === 'FNR' && valgtAdresseType() !== adressetyper.utland
	}

	const manglerPoppOpptjening = () => {
		const harApfPrivat =
			formMethods.watch(`${alderspensjonPath}.inkluderAfpPrivat`) ||
			formMethods.watch(`${alderspensjonPath}.afpPrivatResultat`)
		const poppOpptjeningFom = formMethods.watch('pensjonforvalter.inntekt.fomAar')
		const poppOpptjeningTom = formMethods.watch('pensjonforvalter.inntekt.tomAar')
		const poppOpptjeningAntallAar = poppOpptjeningTom - poppOpptjeningFom
		const poppOpptjeningBelop = formMethods.watch('pensjonforvalter.inntekt.belop')
		return harApfPrivat && (poppOpptjeningAntallAar < 30 || !poppOpptjeningBelop)
	}

	const soknad = formMethods.watch(`${alderspensjonPath}.soknad`)

	const VEDTAK = 'Vedtak'
	const SOKNAD = 'Søknad'

	return (
		<Vis attributt={alderspensjonPath}>
			<Panel
				heading="Alderspensjon"
				hasErrors={panelError(alderspensjonPath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [alderspensjonPath])}
			>
				{nyBestilling && (!harAlder || !harGyldigAlder) && (
					<StyledAlert variant={'warning'} size={'small'}>
						For å sikre at personen har rett på alderspensjon må alder settes til 62 år eller
						høyere. Eventuelt må iverksettelsesdato settes til etter personen har fylt 62 år.
					</StyledAlert>
				)}
				{((leggTil && alderLeggTilPerson < 62) ||
					(importTestnorge && alderImportertPerson?.some((alder) => alder < 62))) && (
					<StyledAlert variant={'warning'} size={'small'}>
						Personer under 62 år har ikke rett på alderspensjon.
					</StyledAlert>
				)}
				{harUgyldigFoedselsaar() && (
					<StyledAlert variant={'warning'} size={'small'}>
						Personer født før 1944 følger gammelt pensjonsregelverk, som ikke støttes av
						alderspensjon-søknad.
					</StyledAlert>
				)}
				{((nyBestilling && alderNyPerson > 61 && alderNyPerson < 67) ||
					(leggTil && alderLeggTilPerson > 61 && alderLeggTilPerson < 67) ||
					(importTestnorge && alderImportertPerson?.some((alder) => alder > 61 && alder < 67))) &&
					!harPopp && (
						<StyledAlert variant={'info'} size={'small'}>
							Personer med alder fra 62 til 66 år vil kun få AP-vedtak om de har høy nok opptjening
							til å ta ut alderspensjon før 67 år. Opptjening kan legges inn på pensjonsgivende
							inntekt (POPP) ved å huke av for dette valget på forrige side.
						</StyledAlert>
					)}
				{!leggTilPaaGruppe && !harNorskBankkonto && (
					<StyledAlert variant={'info'} size={'small'}>
						Personen må ha norsk bankkontonummer for at det skal fattes vedtak og for at vedtaksbrev
						skal kunne opprettes automatisk.
					</StyledAlert>
				)}
				{!harNorskAdresse() && (
					<StyledAlert variant={'warning'} size={'small'}>
						Personen må ha norsk adresse for at det skal fattes vedtak og for at vedtaksbrev skal
						kunne opprettes automatisk.
					</StyledAlert>
				)}
				{formMethods.getValues(`${alderspensjonPath}.relasjoner[0].sumAvForvArbKapPenInntekt`) &&
					!leggTilPaaGruppe &&
					!harPartner &&
					!harPartnerImportertPerson() && (
						<StyledAlert variant={'info'} size={'small'}>
							Personen må ha ektefelle/partner for at feltet "Ektefelle/partners inntekt" skal være
							gyldig.
						</StyledAlert>
					)}
				{manglerPoppOpptjening() && (
					<StyledAlert variant={'info'} size={'small'}>
						For å sikre at AFP privat innvilges må personen ha minst 30 år med opptjening i POPP (de
						siste 5 årene er spesielt viktige), gjerne med inntekt over gjennomsnittet.
					</StyledAlert>
				)}

				<div className="flexbox--flex-wrap">
					<div className="toggle--wrapper">
						<ToggleGroup
							onChange={(value: string) => {
								formMethods.setValue(
									alderspensjonPath,
									value === VEDTAK
										? { ...genInitialAlderspensjonVedtak }
										: { ...genInitialAlderspensjonSoknad },
								)
							}}
							size={'small'}
							defaultValue={soknad ? SOKNAD : VEDTAK}
							style={{ backgroundColor: '#ffffff' }}
						>
							<ToggleGroup.Item value={VEDTAK} style={{ marginRight: 0 }}>
								{VEDTAK}
							</ToggleGroup.Item>
							<ToggleGroup.Item value={SOKNAD} style={{ marginRight: 0 }}>
								{SOKNAD}
							</ToggleGroup.Item>
						</ToggleGroup>
					</div>
					<Monthpicker
						name={`${alderspensjonPath}.iverksettelsesdato`}
						label="Iverksettelsesdato"
						date={formMethods.getValues(`${alderspensjonPath}.iverksettelsesdato`)}
						handleDateChange={(dato: string) => {
							formMethods.setValue(`${alderspensjonPath}.iverksettelsesdato`, dato, {
								shouldTouch: true,
							})
							formMethods.trigger(`${alderspensjonPath}.iverksettelsesdato`)
						}}
					/>
					{!soknad && (
						<FormSelect
							options={randomSaksbehandlere}
							name={`${alderspensjonPath}.saksbehandler`}
							label={'Saksbehandler'}
						/>
					)}
					{!soknad && (
						<FormSelect
							options={randomAttesterere}
							name={`${alderspensjonPath}.attesterer`}
							label={'Attesterer'}
						/>
					)}
					<FormSelect
						name={`${alderspensjonPath}.uttaksgrad`}
						label="Uttaksgrad"
						options={Options('apUttaksgrad')}
						isClearable={false}
					/>
					{!soknad && (
						<FormSelect
							name={`${alderspensjonPath}.navEnhetId`}
							label="Nav-kontor"
							size={'xxlarge'}
							options={navEnheter}
						/>
					)}
					{soknad && (
						<>
							<FormTextInput
								name={`${alderspensjonPath}.relasjoner[0].sumAvForvArbKapPenInntekt`}
								label="Ektefelle/partners inntekt"
								type="number"
							/>
							<FormCheckbox
								name={`${alderspensjonPath}.inkluderAfpPrivat`}
								label="Inkluder AFP privat"
								checkboxMargin
							/>
							<FormSelect
								name={`${alderspensjonPath}.afpPrivatResultat`}
								label="AFP privat resultat"
								options={Options('afpPrivatResultat')}
							/>
						</>
					)}
				</div>
			</Panel>
		</Vis>
	)
}

AlderspensjonForm.validation = validation
