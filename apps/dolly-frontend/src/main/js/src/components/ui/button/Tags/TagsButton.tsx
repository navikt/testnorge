import React, { useState } from 'react'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import useBoolean from '@/utils/hooks/useBoolean'
import DollyModal from '@/components/ui/modal/DollyModal'
import Button from '@/components/ui/button/Button'
import Loading from '@/components/ui/loading/Loading'

import './TagsButton.less'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import { Alert } from '@navikt/ds-react'

type Props = {
	action: Function
	loading: boolean
	gruppeId: number
	eksisterendeTags: string[]
}

export const TagsButton = ({ action, loading, gruppeId, eksisterendeTags }: Props) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [tags, setTags] = useState(eksisterendeTags)
	const mutate = useMatchMutate()

	if (loading) {
		return <Loading label="Sender tags..." />
	}

	const tagsFraDolly = SelectOptionsOppslag.hentTagsFraDolly()
	const tagOptions = SelectOptionsOppslag.formatOptions('tags', tagsFraDolly)

	return (
		<React.Fragment>
			<Button onClick={openModal} kind="designsystem-link" className="svg-icon-blue">
				TILKNYTT TAGS
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="60%" overflow="auto">
				<div className="tagsModal">
					<div className="tagsModal tagsModal-content">
						<h1>Tilknytt tags</h1>
						<Alert variant={'info'}>
							Tags gir deg mulighet til å identifisere dine PDL-personer på egen “tagged”
							Kafka-topic, der tags[dintag] legges til på responsen. Ta kontakt for ytterligere
							informasjon.
						</Alert>
						<h4>Velg hvilke tags du ønsker å legge til på denne gruppen</h4>
						<DollySelect
							options={tagOptions}
							isLoading={tagsFraDolly.loading}
							size="large"
							isMulti={true}
							value={tags}
							// @ts-ignore
							onChange={(event: []) => setTags(event?.map((ev: { value: string }) => ev.value))}
						/>
					</div>
					<div className="tagsModal-actions">
						<NavButton variant={'danger'} onClick={closeModal}>
							Avbryt
						</NavButton>
						<NavButton
							onClick={() => {
								action(gruppeId, tags).then(() => {
									closeModal()
									return mutate(REGEX_BACKEND_GRUPPER)
								})
							}}
							variant={'primary'}
						>
							Tilknytt tags
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</React.Fragment>
	)
}
