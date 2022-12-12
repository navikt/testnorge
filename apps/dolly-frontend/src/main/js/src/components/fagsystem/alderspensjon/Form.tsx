import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { erForsteEllerTest, panelError } from '~/components/ui/form/formUtils'
import Panel from '~/components/ui/panel/Panel'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import React from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk } from '~/config/kodeverk'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialRelasjon } from '~/components/fagsystem/alderspensjon/initialValues'

const alderspensjonPath = 'pensjonforvalter.alderspensjon'

export const AlderspensjonForm = ({ formikBag }) => (
	<Vis attributt={alderspensjonPath}>
		<Panel
			heading="Alderspensjon (PESYS)"
			hasErrors={panelError(formikBag, alderspensjonPath)}
			iconType="pensjon"
			startOpen={erForsteEllerTest(formikBag.values, [alderspensjonPath])}
			// informasjonstekst={hjelpetekst}
		>
			<div className="flexbox--flex-wrap">
				<FormikDatepicker
					name={`${alderspensjonPath}.iverksettelsesdato`}
					label="Iverksettelsesdato"
					// fastfield={false}
				/>
				<FormikTextInput
					name={`${alderspensjonPath}.uttaksgrad`}
					label="Uttaksgrad"
					type="number"
				/>
				{/*<FormikSelect*/}
				{/*	name={`${alderspensjonPath}.statsborgerskap`}*/}
				{/*	label="Statsborgerskap"*/}
				{/*	kodeverk={AdresseKodeverk.StatsborgerskapLand}*/}
				{/*	size="large"*/}
				{/*	isClearable={false}*/}
				{/*/>*/}
				<FormikSelect
					name={`${alderspensjonPath}.sivilstand`}
					label="Sivilstand"
					options={Options('sivilstandType')}
					isClearable={false}
				/>
				<FormikDatepicker
					name={`${alderspensjonPath}.sivilstatusDatoFom`}
					label="Sivilstand f.o.m. dato"
					// fastfield={false}
				/>
				<FormikCheckbox
					name={`${alderspensjonPath}.flyktning`}
					label="Er flyktning"
					size="small"
					// TODO: Fix x-small
					// checkboxMargin
				/>
				<FormikCheckbox
					name={`${alderspensjonPath}.utvandret`}
					label="Er utvandret"
					size="small"
					// TODO: Fix x-small
					// checkboxMargin
				/>
				{/*	TODO: relasjonListe*/}
				<FormikDollyFieldArray
					name={`${alderspensjonPath}.relasjonListe`}
					header="Relasjoner"
					// hjelpetekst={infotekst}
					newEntry={initialRelasjon}
					buttonText="Relasjon"
					nested
				>
					{(relasjonPath, idx) => {
						return (
							<div key={idx} className="flexbox--flex-wrap">
								<FormikDatepicker
									name={`${relasjonPath}.samboerFraDato`}
									label="Samboer f.o.m. dato"
									// fastfield={false}
								/>
								<FormikDatepicker
									name={`${relasjonPath}.dodsdato`}
									label="Dødsdato"
									// fastfield={false}
								/>
								<FormikCheckbox
									name={`${relasjonPath}.varigAdskilt`}
									label="Er varig adskilt"
									size="small"
									// TODO: Fix x-small
									checkboxMargin
								/>
								<FormikTextInput name={`${relasjonPath}.fnr`} label="Fnr" type="number" />
								<FormikDatepicker
									name={`${relasjonPath}.samlivsbruddDato`}
									label="Dato for samlivsbrudd"
									// fastfield={false}
								/>
								<FormikCheckbox
									name={`${relasjonPath}.harVaertGift`}
									label="Har vært gift"
									size="small"
									// TODO: Fix x-small
									checkboxMargin
								/>
								<FormikCheckbox
									name={`${relasjonPath}.harFellesBarn`}
									label="Har felles barn"
									size="small"
									// TODO: Fix x-small
									checkboxMargin
								/>
								<FormikTextInput
									name={`${relasjonPath}.sumAvForvArbKapPenInntekt`}
									label="Sum pensjonsinntekt"
									type="number"
								/>
								<FormikSelect
									name={`${relasjonPath}.relasjonType`}
									label="Relasjonstype"
									options={Options('sivilstandType')}
								/>
							</div>
						)
					}}
				</FormikDollyFieldArray>
			</div>
		</Panel>
	</Vis>
)
