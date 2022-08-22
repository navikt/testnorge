import React from 'react'
import { validation } from './validation'
import { KontaktinformasjonForDoedsbo } from './partials/kontaktinformasjonForDoedsbo/KontaktinformasjonForDoedsbo'
import { Adresser } from '~/components/fagsystem/pdlf/form/partials/adresser/Adresser'
import { Personinformasjon } from '~/components/fagsystem/pdlf/form/partials/personinformasjon/Personinformasjon'
import { Identifikasjon } from '~/components/fagsystem/pdlf/form/partials/identifikasjon/Identifikasjon'
import { Familierelasjoner } from '~/components/fagsystem/pdlf/form/partials/familierelasjoner/Familierelasjoner'

export const PdlfForm = ({ formikBag }) => (
	<>
		<Personinformasjon formikBag={formikBag} />
		<Adresser formikBag={formikBag} />
		<Familierelasjoner formikBag={formikBag} />
		<Identifikasjon formikBag={formikBag} />
		<KontaktinformasjonForDoedsbo formikBag={formikBag} />
	</>
)

PdlfForm.validation = validation
