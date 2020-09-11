import React from 'react'
import LoadableComponent, { Feilmelding } from '~/components/ui/loading/LoadableComponent'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { ErrorComponent } from '~/components/ui/loading/ErrorComponent'

interface LegeSelect {
	name: string
	label: string
	afterChange: Function
}

type Option = {
	value: string
	label: string
	fnr: string
	fornavn: string
	mellomnavn?: string
	etternavn: string
	hprId: string
}

export default ({ name, label, afterChange }: LegeSelect) => {
	return (
		<LoadableComponent
			onFetch={() =>
				SelectOptionsOppslag.hentLeger().then(response =>
					response.leger.map((lege: Option) => ({
						value: lege.fnr,
						label: `${lege.fnr} - ${lege.fornavn} ${lege.mellomnavn} ${lege.etternavn}`,
						fnr: lege.fnr,
						fornavn: lege.fornavn,
						mellomnavn: lege.mellomnavn,
						etternavn: lege.etternavn,
						hprId: lege.hprId
					}))
				)
			}
			renderOnError={error => {
				return <ErrorComponent errorMessage={error} feilKomponent={'LegeSelect'} />
			}}
			render={(data: Array<Option>) => {
				return (
					<FormikSelect
						name={name}
						label={label}
						options={data}
						type="text"
						size="xxlarge"
						afterChange={afterChange}
						isClearable={false}
					/>
				)
			}}
		/>
	)
}
