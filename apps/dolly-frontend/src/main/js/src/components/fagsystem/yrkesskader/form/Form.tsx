import { useFormContext } from 'react-hook-form'
import React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import {
	initialYrkesskade,
	initialYrkesskadePeriode,
} from '@/components/fagsystem/yrkesskader/initialValues'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { PdlEksisterendePerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDateTimepicker } from '@/components/ui/form/inputs/timepicker/Timepicker'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { OrgnrToggle } from '@/components/fagsystem/inntektsmelding/form/partials/orgnrToogle'
import StyledAlert from '@/components/ui/alert/StyledAlert'

export const yrkesskaderAttributt = 'yrkesskader'

export const YrkesskaderForm = () => {
	const formMethods = useFormContext()

	// const handleChangeInnmelderrolle = (value, path) => {
	// 	formMethods.setValue(`${path}.innmelderrolle`, value?.value)
	// 	formMethods.setValue(`${path}.innmelderIdentifikator`, null)
	// 	formMethods.setValue(`${path}.paaVegneAv`, null)
	// 	formMethods.trigger(path)
	// }

	const handleChangeTidstype = (value, path) => {
		formMethods.setValue(`${path}.tidstype`, value?.value)
		formMethods.setValue(`${path}.skadetidspunkt`, null)
		formMethods.setValue(`${path}.perioder`, [initialYrkesskadePeriode])
		formMethods.trigger(path)
	}

	const manglerVergeEllerForelder = () => {
		// TODO: Sjekk personFoerLeggTil og importPersoner (og ander??)
		const vergemaal = formMethods.watch('pdldata.person.vergemaal')
		const forelder = formMethods
			.watch('pdldata.person.forelderBarnRelasjon')
			?.filter((relasjon) => relasjon?.relatertPersonsRolle === 'FORELDER')
		const harInnmelderrolleVergeOgForesatt = formMethods
			.watch('yrkesskader')
			?.some((yrkesskade) => yrkesskade?.innmelderrolle === 'vergeOgForesatt')
		return (
			harInnmelderrolleVergeOgForesatt &&
			(!vergemaal || vergemaal?.length < 1) &&
			(!forelder || forelder?.length < 1)
		)
	}

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
				iconType="sykdom"
				startOpen={erForsteEllerTest(formMethods.getValues(), [yrkesskaderAttributt])}
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
						// maxEntries={1}
						//TODO: Bare 1 yrkesskade per person???
					>
						{(path: string, idx: number) => {
							return (
								<React.Fragment key={idx}>
									{/*skadelidtIdentifikator: '', // Blir satt av BE*/}

									{/*rolletype: '',*/}
									<FormSelect
										name={`${path}.rolletype`}
										label="Rolletype"
										options={[
											{ value: 'arbeidstaker', label: 'Arbeidstaker' },
											{ value: 'laerling', label: 'Lærling' },
											{
												value: 'arbeidstakerIPetroleum',
												label: 'Arbeidstaker i petroleumsvirksomhet',
											},
										]}
										//TODO: Bruk kodeverk rolletype
										// kodeverk={null}
										size="xlarge"
									/>

									{/*innmelderrolle: '',*/}
									<FormSelect
										name={`${path}.innmelderrolle`}
										label="Innmelderrolle"
										options={[
											{ value: 'denSkadelidte', label: 'Den skadelidte selv' },
											{ value: 'vergeOgForesatt', label: 'Verge/Foresatt' },
											{
												value: 'virksomhetsrepresentant',
												label: 'Virksomhetsrepresentant',
											},
										]}
										//TODO: Bruk kodeverk innmelderrolle
										// kodeverk={null}
										// onChange={(value) => handleChangeInnmelderrolle(value, path)}
										size="large"
									/>

									{/*innmelderIdentifikator: '',*/}
									{/*{formMethods.watch(`${path}.innmelderrolle`) === 'vergeOgForesatt' && (*/}
									{/*	<PdlEksisterendePerson*/}
									{/*		eksisterendePersonPath={`${path}.innmelderIdentifikator`}*/}
									{/*		label="Innmelder"*/}
									{/*		formMethods={formMethods}*/}
									{/*		idx={idx}*/}
									{/*	/>*/}
									{/*)}*/}

									{/*paaVegneAv: '',*/}
									{/*{formMethods.watch(`${path}.innmelderrolle`) === 'virksomhetsrepresentant' && (*/}
									{/*	<OrgnrToggle*/}
									{/*		path={`${path}.paaVegneAv`}*/}
									{/*		formMethods={formMethods}*/}
									{/*		label="På vegne av"*/}
									{/*	/>*/}
									{/*)}*/}

									{/*klassifisering: '',*/}
									<FormSelect
										name={`${path}.klassifisering`}
										label="Klassifisering"
										options={Options('klassifisering')}
										size="large"
									/>

									{/*referanse: '',*/}
									<FormTextInput name={`${path}.referanse`} label="Referanse" size="large" />

									{/*ferdigstillSak: '',*/}
									<FormSelect
										name={`${path}.ferdigstillSak`}
										label="Ferdigsstill sak"
										options={Options('ferdigstillSak')}
									/>

									{/*tidstype: '',*/}
									<FormSelect
										name={`${path}.tidstype`}
										label="Tidstype"
										options={Options('tidstype')}
										size="medium"
										onChange={(value) => handleChangeTidstype(value, path)}
									/>

									{/*skadetidspunkt: null,*/}
									{formMethods.watch(`${path}.tidstype`) === 'tidspunkt' && (
										<FormDateTimepicker
											formMethods={formMethods}
											name={`${path}.skadetidspunkt`}
											label="Skadetidspunkt"
											// date={}
											// onChange={}
										/>
									)}

									{/*perioder: [initialYrkesskadePeriode],*/}
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
														<FormDatepicker
															name={`${periodePath}.fra`}
															label="Fra dato"
															// disabled={}
															// maxDate={}
														/>
														<FormDatepicker
															name={`${periodePath}.til`}
															label="Til dato"
															// disabled={}
															// maxDate={}
														/>
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

YrkesskaderForm.validation = {}
