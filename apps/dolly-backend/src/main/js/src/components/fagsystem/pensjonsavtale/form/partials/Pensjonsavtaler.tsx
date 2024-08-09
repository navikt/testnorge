import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import * as React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import {
	initialNyPensjonsavtale,
	initialNyPensjonsavtaleVerdier,
} from '@/components/fagsystem/pensjonsavtale/initalValues'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

export const PensjonsavtalerForm = ({ formMethods }) => {
	const avtalePath = 'pensjonforvalter.pensjonsavtale'

	return (
		<Vis attributt={avtalePath}>
			<FormDollyFieldArray
				name={avtalePath}
				header="Pensjonsavtale"
				newEntry={initialNyPensjonsavtaleVerdier}
				buttonText="Utbetalingsperiode"
				nested
			>
				{(avtalePath) => (
					<>
						<div className="flexbox--flex-wrap">
							<FormTextInput
								name={`${avtalePath}.startAlderAar`}
								label="Startalder År"
								type="number"
							/>
						</div>
						<div className="flexbox--flex-wrap">
							<FormTextInput
								name={`${avtalePath}.startAlderMaaneder`}
								label="Startalder Måneder"
								type="number"
							/>
						</div>
						<div className="flexbox--flex-wrap">
							<FormTextInput
								name={`${avtalePath}.sluttAlderAar`}
								label="Sluttalder År"
								type="number"
							/>
						</div>
						<div className="flexbox--flex-wrap">
							<FormTextInput
								name={`${avtalePath}.sluttAlderMaaneder`}
								label="Sluttalder Måneder"
								type="number"
							/>
						</div>
						<div className="flexbox--flex-wrap">
							<FormTextInput
								name={`${avtalePath}.aarligUtbetaling`}
								label="Årlig utbetaling"
								type="number"
							/>
						</div>
						<div className="flexbox--flex-wrap">
							<FormTextInput name={`${avtalePath}.grad`} label="Grad" type="number" />
						</div>
						<EraseFillButtons
							formMethods={formMethods}
							path={avtalePath}
							initialFill={initialNyPensjonsavtaleVerdier}
							initialErase={initialNyPensjonsavtale}
						/>
					</>
				)}
			</FormDollyFieldArray>
		</Vis>
	)
}
