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

export const yrkesskaderAttributt = 'yrkesskader'

export const YrkesskaderForm = () => {
	const formMethods = useFormContext()

	// TODO: Krav om arbeidsgiver dersom innmelderrolle === virksomhetsrepresentant

	return (
		<Vis attributt={yrkesskaderAttributt}>
			<Panel
				heading="Yrkesskader"
				hasErrors={panelError(yrkesskaderAttributt)}
				iconType="sykdom"
				startOpen={erForsteEllerTest(formMethods.getValues(), [yrkesskaderAttributt])}
			>
				<FormDollyFieldArray
					name={yrkesskaderAttributt}
					header={'Yrkesskade'}
					newEntry={initialYrkesskade}
					canBeEmpty={false}
				>
					{(path: string, idx: number) => {
						return (
							<React.Fragment key={idx}>
								{/*skadelidtIdentifikator: '', //TODO: Blir satt av BE*/}

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
									//TODO: onChange for å nullstille innmelderIdentifikator og paavegneAv
									// onChange={}
									size="large"
								/>

								{/*innmelderIdentifikator: '',*/}
								{formMethods.watch(`${path}.innmelderrolle`) === 'vergeOgForesatt' && (
									<PdlEksisterendePerson
										eksisterendePersonPath={`${path}.innmelderIdentifikator`}
										label="Innmelder"
										formMethods={formMethods}
										idx={idx}
									/>
								)}

								{/*paaVegneAv: '',*/}
								{formMethods.watch(`${path}.innmelderrolle`) === 'virksomhetsrepresentant' && (
									<OrgnrToggle
										path={`${path}.paaVegneAv`}
										formMethods={formMethods}
										label="På vegne av"
									/>
								)}

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
									//TODO: onChange for å nullstille skadetidspunkt og perioder
									// onChange={}
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
			</Panel>
		</Vis>
	)
}

YrkesskaderForm.validation = {}
