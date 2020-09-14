import * as React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import { DollyApi } from '~/service/Api'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import LoadableComponent, { Feilmelding } from '~/components/ui/loading/LoadableComponent'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorMessage, FastField } from 'formik'
import { FormikField } from '~/components/ui/form/FormikField'
import Icon from '~/components/ui/icon/Icon'
import { DollyErrorAlert } from '~/components/ui/loading/DollyErrorAlert'

interface JournalpostId {
	system: string
	ident: string
}

type Response = {
	miljoe: string
	transaksjonId: string
}

export default ({ system, ident }: JournalpostId) => (
	<LoadableComponent
		onFetch={() => {
			return DollyApi.getTransaksjonid(system, ident).then(
				({ data }): Array<Response> => {
					return data.map((id: Response) => ({
						transaksjonId: id.transaksjonId,
						miljoe: id.miljoe
					}))
				}
			)
		}}
		render={(data: Array<Response>) => {
			return (
				<DollyFieldArray data={data} header="Journalpost-Id" nested>
					{(id: Response, idx: number) => {
						const transaksjonId = JSON.parse(id.transaksjonId)
						return (
							<div key={idx} className="person-visning_content">
								<TitleValue title="Miljø" value={id.miljoe} />
								<TitleValue title="Journalpost-id" value={transaksjonId.journalpostId} />
								<TitleValue title="Dokumentinfo-id" value={transaksjonId.dokumentInfoId} />
							</div>
						)
					}}
				</DollyFieldArray>
			)
		}}
	/>
)
