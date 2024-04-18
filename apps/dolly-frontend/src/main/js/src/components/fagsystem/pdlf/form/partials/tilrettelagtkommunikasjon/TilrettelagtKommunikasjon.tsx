import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { PersoninformasjonKodeverk } from '@/config/kodeverk'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialTilrettelagtKommunikasjon } from '@/components/fagsystem/pdlf/form/initialValues'

export const TilrettelagtKommunikasjon = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={'pdldata.person.tilrettelagtKommunikasjon'}
				header="Tolk"
				newEntry={initialTilrettelagtKommunikasjon}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => (
					<>
						<FormSelect
							name={`${path}.spraakForTaletolk`}
							label="Talespråk"
							kodeverk={PersoninformasjonKodeverk.Spraak}
						/>
						<FormSelect
							name={`${path}.spraakForTegnspraakTolk`}
							label="Tegnspråk"
							kodeverk={PersoninformasjonKodeverk.Spraak}
						/>
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)}
			</FormDollyFieldArray>
		</div>
	)
}
