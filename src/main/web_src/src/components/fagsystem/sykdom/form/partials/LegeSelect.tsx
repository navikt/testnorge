import React from 'react'
import LoadableComponent, { Feilmelding } from '~/components/ui/loading/LoadableComponent'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

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
				SelectOptionsOppslag.hentHelsepersonell().then(response =>
					response.helsepersonell.map((helsepersonell: Option) => ({
						value: helsepersonell.fnr,
						label: `${helsepersonell.fnr} - ${helsepersonell.fornavn} ${helsepersonell.mellomnavn} ${helsepersonell.etternavn}`,
						fnr: helsepersonell.fnr,
						fornavn: helsepersonell.fornavn,
						mellomnavn: helsepersonell.mellomnavn,
						etternavn: helsepersonell.etternavn,
						hprId: helsepersonell.hprId
					}))
				)
			}
			render={(data: Array<Option>, feilmelding: Feilmelding) => {
				return (
					<FormikSelect
						name={name}
						label={label}
						options={data}
						type="text"
						size="xxlarge"
						afterChange={afterChange}
						isClearable={false}
						feil={feilmelding}
					/>
				)
			}}
		/>
	)
}
