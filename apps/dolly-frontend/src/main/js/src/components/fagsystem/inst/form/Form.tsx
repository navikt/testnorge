import { useEffect } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { erForsteEllerTest, usePanelError } from '@/components/ui/form/formUtils'
import Panel from '@/components/ui/panel/Panel'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import {
	DollyFaBlokk,
	DollyFieldArrayWrapper,
	FieldArrayAddButton,
} from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { validation } from '@/components/fagsystem/inst/form/validation'
import { useFieldArray, useFormContext, useWatch } from 'react-hook-form'
import { Tag } from '@navikt/ds-react'

export const initialValues = {
	institusjonstype: '',
	startdato: '',
	forventetSluttdato: '',
	sluttdato: '',
}
export const instAttributt = 'instdata'

const MiljoTag = ({ miljo }: { miljo: string }) => {
	return (
		<Tag variant="strong" size="xsmall" data-color="info" style={{ marginLeft: '10px' }}>
			{miljo?.toUpperCase()}
		</Tag>
	)
}

export const InstForm = () => {
	const formMethods = useFormContext()

	const { fields, append, remove } = useFieldArray({
		control: formMethods.control,
		name: instAttributt,
	})

	const handleAppend = () => {
		append(initialValues)
		void formMethods.trigger(instAttributt)
	}

	const handleRemove = (idx: number) => {
		remove(idx)
		void formMethods.trigger(instAttributt)
	}

	const datoer = useWatch({ control: formMethods.control, name: instAttributt })
		?.map((opphold: any) => `${opphold?.startdato}|${opphold?.sluttdato}`)
		.join(',')

	useEffect(() => {
		const opphold = formMethods.getValues(instAttributt) || []
		const datofeltMedFeil = opphold
			.flatMap((_: any, idx: number) => [
				`${instAttributt}.${idx}.startdato`,
				`${instAttributt}.${idx}.sluttdato`,
			])
			.filter((felt: string) => formMethods.getFieldState(felt).error)
		if (datofeltMedFeil.length > 0) {
			void formMethods.trigger(datofeltMedFeil)
		}
	}, [datoer])

	return (
		<Vis attributt={instAttributt}>
			<Panel
				heading="Institusjonsopphold"
				hasErrors={usePanelError(instAttributt)}
				iconType="institusjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [instAttributt])}
			>
				<ErrorBoundary>
					<DollyFieldArrayWrapper>
						{fields.map((field, idx) => {
							const path = `${instAttributt}.${idx}`
							const erEksisterendeOpphold = !!formMethods.getValues(`${path}.oppholdId`)

							return (
								<DollyFaBlokk
									key={field.key}
									idx={idx}
									number={idx + 1}
									header={erEksisterendeOpphold ? 'Opphold (eksisterende)' : 'Opphold'}
									handleRemove={() => handleRemove(idx)}
									showDeleteButton={!erEksisterendeOpphold}
									hjelpetekst={null}
									whiteBackground={false}
									tag={
										erEksisterendeOpphold ? (
											<MiljoTag miljo={formMethods.getValues(`${path}.miljo`)} />
										) : null
									}
								>
									<FormSelect
										name={`${path}.institusjonstype`}
										label="Institusjonstype"
										options={Options('institusjonstype')}
										isClearable={false}
										isDisabled={erEksisterendeOpphold}
									/>
									<FormDatepicker name={`${path}.startdato`} label="Startdato" />
									<FormDatepicker name={`${path}.forventetSluttdato`} label="Forventet sluttdato" />
									<FormDatepicker name={`${path}.sluttdato`} label="Sluttdato" />
								</DollyFaBlokk>
							)
						})}
						<FieldArrayAddButton addEntryButtonText="Opphold" onClick={handleAppend} />
					</DollyFieldArrayWrapper>
				</ErrorBoundary>
			</Panel>
		</Vis>
	)
}

InstForm.validation = validation
