import React from 'react'
// @ts-ignore
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
// @ts-ignore
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialTilrettelagtKommunikasjon } from '~/components/fagsystem/pdlf/form/initialValues'

export const TilrettelagtKommunikasjon = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.tilrettelagtKommunikasjon'}
				header="Tolk"
				newEntry={initialTilrettelagtKommunikasjon}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<>
						<FormikSelect
							name={`${path}.spraakForTaletolk`}
							label="Talespraak"
							kodeverk={PersoninformasjonKodeverk.Spraak}
						/>
						<FormikSelect
							name={`${path}.spraakForTegnspraakTolk`}
							label="Tegnspraak"
							kodeverk={PersoninformasjonKodeverk.Spraak}
						/>
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
