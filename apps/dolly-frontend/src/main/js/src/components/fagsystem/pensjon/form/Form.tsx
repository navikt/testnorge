import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from '@/components/fagsystem/pensjon/form/validation'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import React, { useContext } from 'react'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import * as _ from 'lodash'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'

export const pensjonPath = 'pensjonforvalter.inntekt'

const hjelpetekst =
	'Hvis nedjuster med grunnbeløp er valgt skal beløp angis som årsbeløp i dagens kroneverdi, ' +
	'og vil nedjusteres basert på snitt grunnbeløp i inntektsåret.'

export const PensjonForm = ({ formMethods }) => {
	const opts = useContext(BestillingsveilederContext)
	const { nyBestilling, nyBestillingFraMal } = opts?.is

	function kalkulerIdentFyltSyttenAar(values: UseFormReturn['getValues']) {
		const curDate = new Date()
		const alder =
			_.has(values, 'pdldata.opprettNyPerson.foedtFoer') &&
			_.get(values, 'pdldata.opprettNyPerson.foedtFoer') !== null
				? curDate.getFullYear() -
				  // @ts-ignore
				  new Date(_.get(values, 'pdldata.opprettNyPerson.foedtFoer')).getFullYear()
				: _.get(values, 'pdldata.opprettNyPerson.alder')
		return alder && curDate.getFullYear() - alder + 17
	}

	const syttenFraOgMedAar = kalkulerIdentFyltSyttenAar(formMethods.getValues())
	const minAar = new Date().getFullYear() - 17
	const valgtAar = _.get(formMethods.getValues(), `${pensjonPath}.fomAar`)

	return (
		<Vis attributt={pensjonPath}>
			<Panel
				heading="Pensjonsgivende inntekt (POPP)"
				hasErrors={panelError(formMethods.formState.errors, pensjonPath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [pensjonPath])}
				informasjonstekst={hjelpetekst}
			>
				{/*// @ts-ignore*/}
				{!_.has(formMethods.getValues(), 'pdldata.opprettNyPerson.alder') &&
					valgtAar < minAar &&
					(nyBestilling || nyBestillingFraMal) && (
						<StyledAlert variant={'info'} size={'small'}>
							Pensjonsgivende inntekt kan settes fra året personen fyller 17 år. For å sikre at
							personen får gyldig alder kan denne settes ved å huke av for "Alder" på forrige side.
						</StyledAlert>
					)}
				<Kategori title="Pensjonsgivende inntekt" vis={pensjonPath}>
					<div className="flexbox--flex-wrap">
						<FormikSelect
							name={`${pensjonPath}.fomAar`}
							label="Fra og med år"
							options={getYearRangeOptions(syttenFraOgMedAar || 1968, new Date().getFullYear() - 1)}
							isClearable={false}
						/>

						<FormikSelect
							name={`${pensjonPath}.tomAar`}
							label="Til og med år"
							options={getYearRangeOptions(1968, new Date().getFullYear() - 1)}
							isClearable={false}
						/>

						<FormikTextInput
							name={`${pensjonPath}.belop`}
							label="Beløp"
							type="number"
							fastfield="false"
						/>

						<FormikCheckbox
							name={`${pensjonPath}.redusertMedGrunnbelop`}
							label="Nedjuster med grunnbeløp"
							size="small"
							checkboxMargin
						/>
					</div>
				</Kategori>
			</Panel>
		</Vis>
	)
}

PensjonForm.validation = validation
