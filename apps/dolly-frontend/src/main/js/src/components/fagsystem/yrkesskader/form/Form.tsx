import { useFormContext } from 'react-hook-form'
import React, { useContext } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import {
	initialYrkesskade,
	initialYrkesskadePeriode,
} from '@/components/fagsystem/yrkesskader/initialValues'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import { validation } from '@/components/fagsystem/yrkesskader/form/validation'
import { useYrkesskadeKodeverk } from '@/utils/hooks/useYrkesskade'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { YrkesskadeTypes } from '@/components/fagsystem/yrkesskader/YrkesskaderTypes'

export const yrkesskaderAttributt = 'yrkesskader'

export const YrkesskaderForm = () => {
	const formMethods = useFormContext()
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const handleChangeTidstype = (value: any, path: string) => {
		formMethods.setValue(`${path}.tidstype`, value?.value || null)
		formMethods.setValue(`${path}.skadetidspunkt`, null)
		if (value?.value === 'periode') {
			formMethods.setValue(`${path}.perioder`, [initialYrkesskadePeriode])
		} else {
			formMethods.setValue(`${path}.perioder`, null)
		}
		formMethods.trigger(path)
	}

	const getVergemaal = () => {
		const vergemaalForm = formMethods.watch('pdldata.person.vergemaal')
		const vergemaalImport = opts?.importPersoner?.flatMap(
			(person: any) => person?.data?.hentPerson?.vergemaalEllerFremtidsfullmakt,
		)
		const vergemaalLeggTil =
			opts?.personFoerLeggTil?.pdl?.hentPerson?.vergemaalEllerFremtidsfullmakt ||
			opts?.personFoerLeggTil?.pdlforvalter?.person?.vergemaal

		return vergemaalForm || vergemaalImport || vergemaalLeggTil
	}

	const getForelder = () => {
		const forelderForm = formMethods
			.watch('pdldata.person.forelderBarnRelasjon')
			?.filter((relasjon: any) => relasjon?.relatertPersonsRolle === 'FORELDER')
		const forelderImport = opts?.importPersoner
			?.flatMap((person: any) => person?.data?.hentPerson?.forelderBarnRelasjon)
			?.filter((relasjon: any) => relasjon?.minRolleForPerson === 'BARN')
		const forelderLeggTil = (
			opts?.personFoerLeggTil?.pdl?.hentPerson?.forelderBarnRelasjon ||
			opts?.personFoerLeggTil?.pdlforvalter?.person?.forelderBarnRelasjon
		)?.filter((relasjon: any) => relasjon?.minRolleForPerson === 'BARN')

		return forelderForm || forelderImport || forelderLeggTil
	}

	const manglerVergeEllerForelder = () => {
		const vergemaal = getVergemaal()
		const forelder = getForelder()
		const harInnmelderrolleVergeOgForesatt = formMethods
			.watch('yrkesskader')
			?.some((yrkesskade: YrkesskadeTypes) => yrkesskade?.innmelderrolle === 'vergeOgForesatt')

		return (
			harInnmelderrolleVergeOgForesatt &&
			(!vergemaal || vergemaal?.length < 1) &&
			(!forelder || forelder?.length < 1)
		)
	}

	const { kodeverkData: kodeverkRolletype } = useYrkesskadeKodeverk('ROLLETYPE')
	const { kodeverkData: kodeverkInnmelderrolletype } = useYrkesskadeKodeverk('INNMELDERROLLETYPE')

	const rolletypeOptions =
		kodeverkRolletype &&
		Object.values(kodeverkRolletype)?.map((option: any) => ({
			value: option?.kode,
			label: option?.verdi,
		}))

	const innmelderrolletypeOptions =
		kodeverkInnmelderrolletype &&
		Object.values(kodeverkInnmelderrolletype)?.map((option: any) => ({
			value: option?.kode,
			label: option?.verdi,
		}))

	const hjelpetekst = (
		<>
			<p>
				Det er kun rolletyper som blir rutet til KOMPYS som vil få opprettet en synlig sak. Saker
				som går til GOSYS må manuelt behandles før de kan dukke opp i Infotrygd og deretter dukke
				opp i saksliste.
			</p>
			<p>Foreløpige kriterier for ruting til Kompys er:</p>
			<ul>
				<li>Det må være bagatellmessige skader.</li>
				<li>Bruker kan ikke ha en eksisterende sak.</li>
				<li>
					Bruker kan ikke ha en potensiell innkommende sak/oppgave/behandling (sjekkes i Joark).
				</li>
				<li>
					Rolletype er elevEllerStudent, vernepliktigIFoerstegangstjenesten eller
					vernepliktigIRepetisjonstjeneste (utvides stadig støtte for flere rolletyper).
				</li>
			</ul>
		</>
	)

	return (
		<Vis attributt={yrkesskaderAttributt}>
			<Panel
				heading="Yrkesskader"
				hasErrors={panelError(yrkesskaderAttributt)}
				// @ts-ignore
				iconType="sykdom"
				startOpen={erForsteEllerTest(formMethods.getValues(), [yrkesskaderAttributt])}
				// @ts-ignore
				informasjonstekst={hjelpetekst}
			>
				<>
					{manglerVergeEllerForelder() && (
						<StyledAlert variant={'info'} size={'small'}>
							Person må ha verge, eller forelder dersom alder er 5-17 år. Dette kan legges til ved å
							huke av for "Vergemål" eller "Har barn/foreldre" på forrige side.
						</StyledAlert>
					)}
					<FormDollyFieldArray
						name={yrkesskaderAttributt}
						header={'Yrkesskade'}
						newEntry={initialYrkesskade}
						canBeEmpty={false}
					>
						{(path: string, idx: number) => {
							return (
								<React.Fragment key={idx}>
									<FormSelect
										name={`${path}.rolletype`}
										label="Rolletype"
										options={rolletypeOptions}
										size="xlarge"
									/>
									<FormSelect
										name={`${path}.innmelderrolle`}
										label="Innmelderrolle"
										options={innmelderrolletypeOptions}
										size="large"
										isClearable={false}
									/>
									<FormSelect
										name={`${path}.klassifisering`}
										label="Klassifisering"
										options={Options('klassifisering')}
										size="large"
									/>
									<FormTextInput name={`${path}.referanse`} label="Referanse" size="large" />
									<FormSelect
										name={`${path}.ferdigstillSak`}
										label="Ferdigsstill sak"
										options={Options('ferdigstillSak')}
									/>
									<FormSelect
										name={`${path}.tidstype`}
										label="Tidstype"
										options={Options('tidstype')}
										size="medium"
										onChange={(value: any) => handleChangeTidstype(value, path)}
									/>
									{formMethods.watch(`${path}.tidstype`) === 'tidspunkt' && (
										<FormDatepicker
											name={`${path}.skadetidspunkt`}
											label="Skadetidspunkt"
											format={'DD.MM.YYYY HH:mm'}
										/>
									)}
									{formMethods.watch(`${path}.tidstype`) === 'periode' && (
										<FormDollyFieldArray
											name={`${path}.perioder`}
											header={'Perioder med sykdom'}
											newEntry={initialYrkesskadePeriode}
											canBeEmpty={false}
											nested
										>
											{(periodePath: string, periodeIdx: number) => {
												return (
													<React.Fragment key={periodeIdx}>
														<FormDatepicker name={`${periodePath}.fra`} label="Fra dato" />
														<FormDatepicker name={`${periodePath}.til`} label="Til dato" />
													</React.Fragment>
												)
											}}
										</FormDollyFieldArray>
									)}
								</React.Fragment>
							)
						}}
					</FormDollyFieldArray>
				</>
			</Panel>
		</Vis>
	)
}

YrkesskaderForm.validation = validation
