import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { erForsteEllerTest, panelError } from '~/components/ui/form/formUtils'
import Panel from '~/components/ui/panel/Panel'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import React, { useContext } from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialRelasjon } from '~/components/fagsystem/alderspensjon/form/initialValues'
import { Alert } from '@navikt/ds-react'
import styled from 'styled-components'
import _has from 'lodash/has'
import _get from 'lodash/get'
import { add, isDate } from 'date-fns'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { validation } from '~/components/fagsystem/alderspensjon/form/validation'
import { Monthpicker } from '~/components/ui/form/inputs/monthpicker/Monthpicker'

const StyledAlert = styled(Alert)`
	margin-bottom: 20px;
	&&& {
		.navds-alert__wrapper {
			max-width: 60rem;
		}
	}
`

const alderspensjonPath = 'pensjonforvalter.alderspensjon'

export const AlderspensjonForm = ({ formikBag }) => {
	const opts = useContext(BestillingsveilederContext)
	const isLeggTil = opts?.is?.leggTil

	const harAlder =
		_has(formikBag.values, 'pdldata.opprettNyPerson.alder') &&
		_has(formikBag.values, 'pdldata.opprettNyPerson.foedtFoer')

	const alder = _get(formikBag.values, 'pdldata.opprettNyPerson.alder')
	const foedtFoer = _get(formikBag.values, 'pdldata.opprettNyPerson.foedtFoer')
	const harUgyldigAlder =
		(alder && alder < 62) || (isDate(foedtFoer) && add(foedtFoer, { years: 62 }) > new Date())

	return (
		<Vis attributt={alderspensjonPath}>
			<Panel
				heading="Alderspensjon (PESYS)"
				hasErrors={panelError(formikBag, alderspensjonPath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formikBag.values, [alderspensjonPath])}
			>
				{!harAlder && !isLeggTil && (
					<StyledAlert variant={'warning'} size={'small'}>
						For å sikre at personen har rett på alderspensjon må alder velges på forrige side, og
						settes til 62 år eller høyere.
					</StyledAlert>
				)}
				{harUgyldigAlder && !isLeggTil && (
					<StyledAlert variant={'warning'} size={'small'}>
						For å sikre at personen har rett på alderspensjon må alder settes til 62 år eller
						høyere.
					</StyledAlert>
				)}
				<div className="flexbox--flex-wrap">
					<Monthpicker
						name={`${alderspensjonPath}.iverksettelsesdato`}
						label="Iverksettelsesmåned"
						date={_get(formikBag.values, `${alderspensjonPath}.iverksettelsesdato`)}
						handleDateChange={(dato: string) =>
							formikBag.setFieldValue(`${alderspensjonPath}.iverksettelsesdato`, dato)
						}
					/>
					<FormikSelect
						name={`${alderspensjonPath}.uttaksgrad`}
						label="Uttaksgrad"
						options={Options('apUttaksgrad')}
						isClearable={false}
					/>
					<FormikSelect
						name={`${alderspensjonPath}.sivilstand`}
						label="Sivilstand"
						options={Options('sivilstandType')}
						isClearable={false}
					/>
					<FormikDatepicker
						name={`${alderspensjonPath}.sivilstatusDatoFom`}
						label="Sivilstand f.o.m. dato"
					/>
					<div className="flexbox--flex-wrap">
						<FormikCheckbox
							name={`${alderspensjonPath}.flyktning`}
							label="Er flyktning"
							size="small"
						/>
						<FormikCheckbox
							name={`${alderspensjonPath}.utvandret`}
							label="Er utvandret"
							size="small"
						/>
					</div>
					<FormikDollyFieldArray
						name={`${alderspensjonPath}.relasjonListe`}
						header="Relasjoner"
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
									/>
									<FormikDatepicker name={`${relasjonPath}.dodsdato`} label="Dødsdato" />
									<FormikCheckbox
										name={`${relasjonPath}.varigAdskilt`}
										label="Er varig adskilt"
										size="small"
										checkboxMargin
									/>
									<FormikTextInput name={`${relasjonPath}.fnr`} label="Fnr" type="number" />
									<FormikDatepicker
										name={`${relasjonPath}.samlivsbruddDato`}
										label="Dato for samlivsbrudd"
									/>
									<FormikCheckbox
										name={`${relasjonPath}.harVaertGift`}
										label="Har vært gift"
										size="small"
										checkboxMargin
									/>
									<FormikCheckbox
										name={`${relasjonPath}.harFellesBarn`}
										label="Har felles barn"
										size="small"
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
}

AlderspensjonForm.validation = validation
