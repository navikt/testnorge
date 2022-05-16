import React, { useEffect } from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError, erForste } from '~/components/ui/form/formUtils'
import { validation } from '~/components/fagsystem/tjenestepensjon/form/validation'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { YtelseForm } from '~/components/fagsystem/tjenestepensjon/form/partials/ytelseForm'
import { subDays } from 'date-fns'
import { PensjonApi } from '~/service/Api'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

const path = 'pensjonforvalter.tp'
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
		const ordninger = PensjonApi.getTpOrdninger()
		ordninger.then((json) => {
			const tpOrdninger = json.data.map((data) => {
				return { value: data.tpnr, label: `${data.tpnr} ${data.navn}` }
			})
			Options('tpOrdninger').push(...tpOrdninger)
		})
	}
}

export const TjenestepensjonForm = ({ formikBag }) => {
	useEffect(() => {
		fetchTpOrdninger()
	}, [Options('tpOrdninger')])

	return (
		<Vis attributt={path}>
			<Panel
				heading="Tjenestepensjon"
				hasErrors={panelError(formikBag, path)}
				iconType="pensjon"
				startOpen={erForste(formikBag.values, [path])}
				informasjonstekst={hjelpetekst}
			>
				<FormikDollyFieldArray
					name="pensjonforvalter.tp"
					header="Ordning"
					newEntry={initialOrdning}
				>
					{(formPath, idx) => (
						<React.Fragment key={idx}>
							<React.Fragment>
								<div className="flexbox--flex-wrap">
									<FormikSelect
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
				</FormikDollyFieldArray>
			</Panel>
		</Vis>
	)
}

TjenestepensjonForm.validation = validation
