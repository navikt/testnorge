import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialFoererkort,
	initialFoererkortVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import * as React from 'react'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import { useFormContext } from 'react-hook-form'

export const FoererkortForm = () => {
	const formMethods = useFormContext()
	const foererkortTyperListePath = 'arbeidsplassenCV.foererkort'

	return (
		<Vis attributt={foererkortTyperListePath}>
			<FormDollyFieldArray
				name={foererkortTyperListePath}
				header="Førerkort"
				newEntry={initialFoererkortVerdier}
				nested
			>
				{(foererkortPath, idx) => (
					<>
						<div key={idx} className="flexbox--flex-wrap">
							<FormSelect
								name={`${foererkortPath}.type`}
								label="Type førerkort"
								options={Options('foererkortTyper')}
								size="large"
								isClearable={false}
							/>
							<FormDatepicker name={`${foererkortPath}.acquiredDate`} label="Gyldig fra" />
							<FormDatepicker name={`${foererkortPath}.expiryDate`} label="Gyldig til" />
						</div>
						<EraseFillButtons
							formMethods={formMethods}
							path={foererkortPath}
							initialErase={initialFoererkort}
							initialFill={initialFoererkortVerdier}
						/>
					</>
				)}
			</FormDollyFieldArray>
		</Vis>
	)
}
