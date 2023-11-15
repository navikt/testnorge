import { validation } from './validation/validation'
import { KontaktinformasjonForDoedsbo } from './partials/kontaktinformasjonForDoedsbo/KontaktinformasjonForDoedsbo'
import { Adresser } from '@/components/fagsystem/pdlf/form/partials/adresser/Adresser'
import { Personinformasjon } from '@/components/fagsystem/pdlf/form/partials/personinformasjon/Personinformasjon'
import { Identifikasjon } from '@/components/fagsystem/pdlf/form/partials/identifikasjon/Identifikasjon'
import { Familierelasjoner } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/Familierelasjoner'

export const PdlfForm = ({ formMethods }) => (
	<>
		<Personinformasjon formMethods={formMethods} />
		<Adresser formMethods={formMethods} />
		<Familierelasjoner formMethods={formMethods} />
		<Identifikasjon formMethods={formMethods} />
		<KontaktinformasjonForDoedsbo formMethods={formMethods} />
	</>
)

PdlfForm.validation = validation
