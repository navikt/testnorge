import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikProps } from 'formik'
import { DoedfoedtBarn } from '~/components/fagsystem/pdlf/form/partials/familierelasjoner/doedfoedtBarn/DoedfoedtBarn'
import { ForelderBarnRelasjon } from '~/components/fagsystem/pdlf/form/partials/familierelasjoner/forelderBarnRelasjon/ForelderBarnRelasjon'
import { Sivilstand } from '~/components/fagsystem/pdlf/form/partials/familierelasjoner/sivilstand/Sivilstand'
import { Foreldreansvar } from '~/components/fagsystem/pdlf/form/partials/familierelasjoner/foreldreansvar/Foreldreansvar'

const relasjonerAttributter = [
	'pdldata.person.sivilstand',
	'pdldata.person.forelderBarnRelasjon',
	'pdldata.person.foreldreansvar',
	'pdldata.person.doedfoedtBarn',
]

export const Familierelasjoner = ({ formikBag }: { formikBag: FormikProps<any> }) => {
	return (
		<Vis attributt={relasjonerAttributter}>
			<Panel
				heading="Familierelasjoner"
				hasErrors={panelError(formikBag, relasjonerAttributter)}
				iconType={'relasjoner'}
				// @ts-ignore
				startOpen={() => erForste(formikBag.values, [relasjonerAttributter])}
				checkAttributeArray={undefined}
				uncheckAttributeArray={undefined}
			>
				<Kategori title="Sivilstand (partner)" vis="pdldata.person.sivilstand">
					<Sivilstand formikBag={formikBag} />
				</Kategori>
				<Kategori title="Barn/foreldre" vis="pdldata.person.forelderBarnRelasjon">
					<ForelderBarnRelasjon formikBag={formikBag} />
				</Kategori>
				<Kategori title="Foreldreansvar" vis="pdldata.person.foreldreansvar">
					<Foreldreansvar formikBag={formikBag} />
				</Kategori>
				<Kategori title="Dødfødt barn" vis="pdldata.person.doedfoedtBarn">
					<DoedfoedtBarn formikBag={formikBag} />
				</Kategori>
			</Panel>
		</Vis>
	)
}
