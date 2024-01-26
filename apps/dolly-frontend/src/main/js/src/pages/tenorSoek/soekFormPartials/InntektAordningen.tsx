import Accordion from '@navikt/ds-react/src/accordion/Accordion'
import React, { SyntheticEvent } from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { createOptions } from '@/pages/tenorSoek/utils'

export const InntektAordningen = ({ formikBag, handleChange, getValue }: any) => {
	const { domain: beskrivelseOptions, loading, error } = useTenorDomain('AOrdningBeskrivelse')
	console.log('formikBag: ', formikBag.values) //TODO - SLETT MEG
	return (
		<Accordion size="small" headingSize="xsmall" className="flexbox--full-width">
			<Accordion.Item defaultOpen={true}>
				<Accordion.Header>
					<span>Inntekt A-ordningen</span>
				</Accordion.Header>
				<Accordion.Content>
					<div className="flexbox--full-width">
						<div className="flexbox--flex-wrap">
							<FormikSelect
								name="inntektAordningen.beskrivelse"
								options={createOptions(beskrivelseOptions?.data)}
								size="large"
								label="Beskrivelse"
								// placeholder="Velg beskrivelse ..."
								onChange={(val: SyntheticEvent) =>
									handleChange(val?.value || null, 'inntektAordningen.beskrivelse')
								}
								value={getValue('inntektAordningen.beskrivelse')}
							/>
						</div>
					</div>
				</Accordion.Content>
			</Accordion.Item>
		</Accordion>
	)
}
