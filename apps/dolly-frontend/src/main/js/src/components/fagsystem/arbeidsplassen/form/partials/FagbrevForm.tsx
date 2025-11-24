import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialFagbrev,
	initialFagbrevVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import * as React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import { useFormContext } from 'react-hook-form'

export const FagbrevForm = () => {
	const formMethods = useFormContext()
	const setFagbrev = (selected, path) => {
		const fagbrev = {
			title: selected.value,
			type: selected.type,
		}
		formMethods.setValue(path, fagbrev)
	}

	const fagbrevListePath = 'arbeidsplassenCV.fagbrev'

	return (
		<Vis attributt={fagbrevListePath}>
			<FormDollyFieldArray
				name={fagbrevListePath}
				header="Fagbrev"
				newEntry={initialFagbrevVerdier}
				nested
			>
				{(fagbrevPath) => (
					<>
						<FormSelect
							name={`${fagbrevPath}.title`}
							label="Fagdokumentasjon"
							options={Options('fagbrev')}
							size="xxlarge"
							isClearable={false}
							onChange={(selected) => setFagbrev(selected, fagbrevPath)}
						/>
						<EraseFillButtons
							formMethods={formMethods}
							path={fagbrevPath}
							initialErase={initialFagbrev}
							initialFill={initialFagbrevVerdier}
						/>
					</>
				)}
			</FormDollyFieldArray>
		</Vis>
	)
}
