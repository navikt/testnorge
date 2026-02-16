import React, { useEffect } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from '@/components/fagsystem/tjenestepensjon/form/validation'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { YtelseForm } from '@/components/fagsystem/tjenestepensjon/form/partials/ytelseForm'
import { subDays } from 'date-fns'
import { PensjonApi } from '@/service/Api'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { useFormContext } from 'react-hook-form'

export const tpPath = 'pensjonforvalter.tp'
const hjelpetekst = 'Ordningen som personen skal ha et forhold til.'

export const initialYtelser = {
	type: 'ALDER',
	datoInnmeldtYtelseFom: subDays(new Date(), 30),
	datoYtelseIverksattFom: subDays(new Date(), 30),
	datoYtelseIverksattTom: null,
}

export const initialOrdning = {
	ordning: '3010',
	ytelser: [initialYtelser],
}

export const fetchTpOrdninger = () => {
	if (!Options('tpOrdninger').length) {
		PensjonApi.getTpOrdninger().then((json) => {
			const tpOrdninger = json?.data?.map((data) => {
				return { value: data.tpnr, label: `${data.tpnr} ${data.navn}` }
			})
			Options('tpOrdninger').push(...tpOrdninger)
		})
	}
}

export const TjenestepensjonForm = () => {
	const formMethods = useFormContext()
	useEffect(() => {
		fetchTpOrdninger()
	}, [Options('tpOrdninger')])

	return (
		<Vis attributt={tpPath}>
			<Panel
				heading="Tjenestepensjon"
				hasErrors={panelError(tpPath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [tpPath])}
				informasjonstekst={hjelpetekst}
			>
				{/*// @ts-ignore*/}
				<FormDollyFieldArray name={tpPath} header="Ordning" newEntry={initialOrdning}>
					{(formPath, idx) => (
						<React.Fragment key={idx}>
							<React.Fragment>
								<div className="flexbox--flex-wrap">
									<FormSelect
										name={`${formPath}.ordning`}
										label="tp-nr"
										size="grow"
										isClearable={false}
										options={Options('tpOrdninger')}
									/>
									<YtelseForm
										path={`${formPath}.ytelser`}
										header="Ytelser"
										initialYtelser={initialYtelser}
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

TjenestepensjonForm.validation = validation
