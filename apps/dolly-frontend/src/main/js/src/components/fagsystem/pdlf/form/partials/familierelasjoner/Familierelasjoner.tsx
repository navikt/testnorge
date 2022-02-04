import React, { useContext, useEffect, useState } from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { Option, SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { FormikProps } from 'formik'
import { DoedfoedtBarn } from '~/components/fagsystem/pdlf/form/partials/familierelasjoner/doedfoedtBarn/DoedfoedtBarn'
import { ForelderBarnRelasjon } from '~/components/fagsystem/pdlf/form/partials/familierelasjoner/forelderBarnRelasjon/ForelderBarnRelasjon'
import { Sivilstand } from '~/components/fagsystem/pdlf/form/partials/familierelasjoner/sivilstand/Sivilstand'

const relasjonerAttributter = [
	'pdldata.person.sivilstand',
	'pdldata.person.forelderBarnRelasjon',
	'pdldata.person.doedfoedtBarn',
]

export const Familierelasjoner = ({ formikBag }: { formikBag: FormikProps<any> }) => {
	const opts = useContext(BestillingsveilederContext)
	const { gruppeId } = opts

	const [identOptions, setIdentOptions] = useState<Array<Option>>([])
	useEffect(() => {
		SelectOptionsOppslag.hentGruppeIdentOptions(gruppeId).then((response: [Option]) =>
			setIdentOptions(response)
		)
	}, [])

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
					<Sivilstand formikBag={formikBag} identOptions={identOptions} />
				</Kategori>
				<Kategori title="Barn/foreldre" vis="pdldata.person.forelderBarnRelasjon">
					<ForelderBarnRelasjon formikBag={formikBag} identOptions={identOptions} />
				</Kategori>
				<Kategori title="Dødfødt barn" vis="pdldata.person.doedfoedtBarn">
					<DoedfoedtBarn formikBag={formikBag} />
				</Kategori>
			</Panel>
		</Vis>
	)
}
