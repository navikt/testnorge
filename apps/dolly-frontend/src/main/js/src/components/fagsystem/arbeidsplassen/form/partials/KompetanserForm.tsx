import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialKompetanser,
	initialKompetanserVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import * as React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import { useFormContext } from 'react-hook-form'

export const KompetanserForm = () => {
	const formMethods = useFormContext()
	const kompetanseListePath = 'arbeidsplassenCV.kompetanser'

	return (
		<Vis attributt={kompetanseListePath}>
			<FormDollyFieldArray
				name={kompetanseListePath}
				header="Kompetanser"
				newEntry={initialKompetanserVerdier}
				buttonText="Kompetanse"
				nested
			>
				{(kompetansePath) => (
					<>
						<FormSelect
							name={`${kompetansePath}.title`}
							label="Kompetanse/ferdighet/verktøy"
							options={Options('kompetanse')}
							size="xxlarge"
							isClearable={false}
						/>
						<EraseFillButtons
							formMethods={formMethods}
							path={kompetansePath}
							initialFill={initialKompetanserVerdier}
							initialErase={initialKompetanser}
						/>
					</>
				)}
			</FormDollyFieldArray>
		</Vis>
	)
}
