import React from 'react'
// @ts-ignore
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
// @ts-ignore
import { AdresseKodeverk, PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { initialStatsborgerskap } from '~/components/fagsystem/pdlf/form/initialValues'

export const Statsborgerskap = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.statsborgerskap'}
				header="Statsborgerskap"
				newEntry={initialStatsborgerskap}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<>
						<FormikSelect
							name={`${path}.landkode`}
							label="Statsborgerskap"
							kodeverk={AdresseKodeverk.StatsborgerskapLand}
							size="large"
							isClearable={false}
						/>
						<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Statsborgerskap fra" />
						<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Statsborgerskap til" />
						<FormikDatepicker name={`${path}.bekreftelsesdato`} label="Bekreftelsesdato" />
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
