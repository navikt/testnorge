import React from 'react'
import { validation } from '@/components/fagsystem/pensjonsavtale/form/validation'
import { useFormContext } from 'react-hook-form'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { UtbetalingsperioderForm } from '@/components/fagsystem/pensjonsavtale/form/partials/Utbetalingsperioder'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialPensjonsavtale,
	initialUtbetalingsperiode,
} from '@/components/fagsystem/pensjonsavtale/initalValues'

export const avtalePath = 'pensjonforvalter.pensjonsavtale'
const hjelpetekst =
	'Pensjonsavtale inneholder beskrivelse av opptjeningsår, samt plangte perioder og grad for uttak.'

export const PensjonsavtaleForm = () => {
	const formMethods = useFormContext()

	return (
		<Vis attributt={avtalePath}>
			<Panel
				heading="Pensjonsavtale"
				hasErrors={panelError(avtalePath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [avtalePath])}
				informasjonstekst={hjelpetekst}
			>
				<FormDollyFieldArray
					name={avtalePath}
					header="Pensjonsavtale"
					newEntry={initialPensjonsavtale}
				>
					{(formPath, idx) => (
						<React.Fragment key={idx}>
							<React.Fragment>
								<div className="flexbox--flex-wrap">
									<FormTextInput
										name={`${formPath}.produktBetegnelse`}
										label="Produktbetegnelse"
										type="string"
									/>

									<FormSelect
										name={`${formPath}.avtaleKategori`}
										label="Avtalekategori"
										size={'medium'}
										options={Options('avtaleKategori')}
										isClearable={false}
									/>

									<UtbetalingsperioderForm
										path={`${formPath}.utbetalingsperioder`}
										initialUtbetalingsperiode={initialUtbetalingsperiode}
									/>
								</div>
							</React.Fragment>
						</React.Fragment>
					)}
				</FormDollyFieldArray>
			</Panel>
		</Vis>
	)
}

PensjonsavtaleForm.validation = validation
