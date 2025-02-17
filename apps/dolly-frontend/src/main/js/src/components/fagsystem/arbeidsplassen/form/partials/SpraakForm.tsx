import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialSpraak,
	initialSpraakVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import * as React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import { useFormContext } from 'react-hook-form'

export const SpraakForm = () => {
	const formMethods = useFormContext()
	const spraakListePath = 'arbeidsplassenCV.spraak'

	return (
		<Vis attributt={spraakListePath}>
			<FormDollyFieldArray
				name={spraakListePath}
				header="Språk"
				newEntry={initialSpraakVerdier}
				nested
			>
				{(spraakPath, idx) => (
					<>
						<div key={idx} className="flexbox--flex-wrap">
							<FormSelect
								name={`${spraakPath}.language`}
								label="Språk"
								options={Options('spraak')}
								size="large"
								isClearable={false}
							/>
							<FormSelect
								name={`${spraakPath}.oralProficiency`}
								label="Muntlig"
								options={Options('spraakNivaa')}
								size="medium"
								isClearable={false}
							/>
							<FormSelect
								name={`${spraakPath}.writtenProficiency`}
								label="Skriftlig"
								options={Options('spraakNivaa')}
								size="medium"
								isClearable={false}
							/>
						</div>
						<EraseFillButtons
							formMethods={formMethods}
							path={spraakPath}
							initialErase={initialSpraak}
							initialFill={initialSpraakVerdier}
						/>
					</>
				)}
			</FormDollyFieldArray>
		</Vis>
	)
}
