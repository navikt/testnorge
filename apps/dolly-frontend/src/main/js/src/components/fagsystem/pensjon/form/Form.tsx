import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from '@/components/fagsystem/pensjon/form/validation'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { DollyTextInput, FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import React, { useContext } from 'react'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import _ from 'lodash'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useFormContext } from 'react-hook-form'
import { usePensjonFacadeGjennomsnitt } from '@/utils/hooks/usePensjonFacade'

export const pensjonPath = 'pensjonforvalter.inntekt'

const hjelpetekst =
	'Hvis nedjuster med grunnbeløp er valgt skal beløp angis som årsbeløp i dagens kroneverdi, ' +
	'og vil nedjusteres basert på snitt grunnbeløp i inntektsåret.'

export const PensjonForm = () => {
	const formMethods = useFormContext()
	const opts = useContext(BestillingsveilederContext)
	const { nyBestilling, nyBestillingFraMal } = opts?.is
	const pensjonSkjemaInntekt = usePensjonFacadeGjennomsnitt(
		formMethods.watch(`${pensjonPath}.generer`),
	)

	function kalkulerIdentFyltSyttenAar() {
		const curDate = new Date()
		const alder =
			formMethods.watch('pdldata.opprettNyPerson.foedtFoer') &&
			formMethods.watch('pdldata.opprettNyPerson.foedtFoer') !== null
				? curDate.getFullYear() -
					// @ts-ignore
					new Date(formMethods.watch('pdldata.opprettNyPerson.foedtFoer')).getFullYear()
				: formMethods.watch('pdldata.opprettNyPerson.alder')
		return alder && curDate.getFullYear() - alder + 17
	}

	const syttenFraOgMedAar = kalkulerIdentFyltSyttenAar()
	const minAar = new Date().getFullYear() - 17
	const valgtAar = formMethods.watch(`${pensjonPath}.fomAar`)

	return (
		<Vis attributt={pensjonPath}>
			<Panel
				heading="Pensjonsgivende inntekt (POPP)"
				hasErrors={panelError(pensjonPath)}
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
				<Kategori title="Generert skjema inntekt" vis={pensjonPath}>
					<div className="flexbox--flex-wrap">
						<FormSelect
							name={`${pensjonPath}.generer.fomAar`}
							label="Fra og med år"
							options={getYearRangeOptions(syttenFraOgMedAar || 1968, new Date().getFullYear() - 1)}
							isClearable={false}
						/>

						<FormSelect
							name={`${pensjonPath}.generer.tomAar`}
							label="Til og med år"
							options={getYearRangeOptions(1968, new Date().getFullYear() - 1)}
							isClearable={false}
						/>

						<DollyTextInput
							name={`${pensjonPath}.generer.averageG`}
							label="Gjenomsnitt G"
							type="number"
						/>

						<FormCheckbox
							name={`${pensjonPath}.generer.tillatInntektUnder1G`}
							label="Nedjuster med grunnbeløp"
							size="small"
							checkboxMargin
						/>
					</div>
				</Kategori>
				<Kategori title="Pensjonsgivende inntekt" vis={pensjonPath}>
					<div className="flexbox--flex-wrap">
						<FormSelect
							name={`${pensjonPath}.fomAar`}
							label="Fra og med år"
							options={getYearRangeOptions(syttenFraOgMedAar || 1968, new Date().getFullYear() - 1)}
							isClearable={false}
						/>

						<FormSelect
							name={`${pensjonPath}.tomAar`}
							label="Til og med år"
							options={getYearRangeOptions(1968, new Date().getFullYear() - 1)}
							isClearable={false}
						/>

						<FormTextInput name={`${pensjonPath}.belop`} label="Beløp" type="number" />

						<FormCheckbox
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
