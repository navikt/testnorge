import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { DollyApi } from '~/service/Api'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import JoarkDokumentService, { Journalpost } from '~/service/services/JoarkDokumentService'
import LoadableComponentWithRetry from '~/components/ui/loading/LoadableComponentWithRetry'
import DokarkivVisning from './DokarkivVisning'

interface Form {
	ident: string
}

type TransaksjonId = {
	transaksjonId: {
		journalpostId: number
	}
	miljoe: string
}

export default ({ ident }: Form) => (
	<div>
		<LoadableComponentWithRetry
			onFetch={() =>
				DollyApi.getTransaksjonid('DOKARKIV', ident)
					.then(({ data }: { data: Array<TransaksjonId> }) =>
						data.map((bestilling: TransaksjonId) =>
							JoarkDokumentService.hentJournalpost(
								bestilling.transaksjonId.journalpostId,
								bestilling.miljoe
							)
						)
					)
					.then((data: Array<Promise<any>>) => Promise.all(data))
			}
			render={(data: Journalpost[]) => {
				const filteredData = data.filter(journalpost => journalpost.journalpostId != null)

				if (!filteredData) {
					return null
				}

				return (
					filteredData &&
					filteredData.length > 0 && (
						<>
							<SubOverskrift label="Dokumenter" iconKind="dokarkiv" />
							<DollyFieldArray data={filteredData} nested={true} ignoreOnSingleElement={true}>
								{(journalpost: Journalpost, idx: number) => (
									<div key={idx} className="person-visning_content">
										<DokarkivVisning journalpost={journalpost} />
									</div>
								)}
							</DollyFieldArray>
						</>
					)
				)
			}}
			label="Laster dokarkiv data"
		/>
	</div>
)
