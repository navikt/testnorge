import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const Diverse = ({ formikProps }) => {
	return (
		<Kategori title="Diverse">
			<FormikSelect name="tpsf.kjonn" label="Kjønn" kodeverk="Kjønnstyper" />

			<FormikSelect name="tpsf.harMellomnavn" label="Mellomnavn" options={Options('boolean')} />

			<FormikSelect name="tpsf.sivilstand" label="Sivilstand" kodeverk="Sivilstander" />

			<FormikSelect name="tpsf.sprakKode" label="Språk" kodeverk="Språk" />

			<FormikDatepicker name="tpsf.egenAnsattDatoFom" label="Egenansatt fra" />

			<FormikSelect name="tpsf.spesreg" label="Diskresjonskode" kodeverk="Diskresjonskoder" />

			<FormikSelect name="tpsf.erForsvunnet" label="Er forsvunnet" options={Options('boolean')} />

			<FormikDatepicker
				name="tpsf.forsvunnetDato"
				label="Forsvunnet dato"
				disabled={!formikProps.values.tpsf.erForsvunnet}
			/>
		</Kategori>
	)
}
