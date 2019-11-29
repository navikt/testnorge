import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import { validation } from './validation'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FalskIdentitet } from './falskIdentitet/FalskIdentitet'
import { UtenlandsId } from './utenlandsId/UtenlandsId'
import { KontaktinformasjonForDoedsbo } from './kontaktinformasjonForDoedsbo/KontaktinformasjonForDoedsbo'

export const PdlfForm = ({ formikBag }) => (
	<React.Fragment>
		<Vis attributt={pathAttrs.kategori.identifikasjon}>
			<Panel heading="Identifikasjon" hasErrors={panelError(formikBag)}>
				<Kategori title="Falsk identitet" vis="pdlforvalter.falskIdentitet">
					<FalskIdentitet formikBag={formikBag} />
				</Kategori>
				<Kategori
					title="Utenlandsk identifikasjonsnummer"
					vis="pdlforvalter.utenlandskIdentifikasjonsnummer"
				>
					<UtenlandsId formikBag={formikBag} />
				</Kategori>
			</Panel>
		</Vis>
		<Vis attributt={pathAttrs.kategori.kontaktinformasjonForDoedsbo}>
			<Panel heading="Kontaktinformasjon for dødsbo" hasErrors={panelError(formikBag)}>
				<KontaktinformasjonForDoedsbo formikBag={formikBag} />
			</Panel>
		</Vis>
	</React.Fragment>
)

PdlfForm.initialValues = attrs => {
	const initial = {
		pdlforvalter: {}
	}

	if (attrs.falskIdentitet)
		initial.pdlforvalter.falskIdentitet = { rettIdentitet: { identitetType: 'UKJENT' } }

	if (attrs.utenlandskIdentifikasjonsnummer)
		initial.pdlforvalter.utenlandskIdentifikasjonsnummer = [
			{ identifikasjonsnummer: '', kilde: '', opphoert: '', utstederland: '' }
		]

	if (attrs.kontaktinformasjonForDoedsbo)
		initial.pdlforvalter.kontaktinformasjonForDoedsbo = {
			adressat: { adressatType: '' },
			adresselinje1: '',
			adresselinje2: '',
			postnummer: '',
			poststed: '',
			landkode: 'NOR',
			skifteform: '',
			utstedtDato: ''
		}

	return _isEmpty(initial.pdlforvalter) ? null : initial
}

PdlfForm.validation = validation
