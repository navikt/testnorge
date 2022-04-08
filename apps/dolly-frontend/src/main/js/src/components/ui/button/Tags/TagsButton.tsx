import React, { useState } from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import useBoolean from '~/utils/hooks/useBoolean'
import DollyModal from '~/components/ui/modal/DollyModal'
import Button from '~/components/ui/button/Button'
import Loading from '~/components/ui/loading/Loading'

import './TagsButton.less'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'

type Props = {
	action: Function
	loading: boolean
	gruppeId: number
	eksisterendeTags: [string]
}

export const TagsButton = ({ action, loading, gruppeId, eksisterendeTags }: Props) => {
	if (loading) return <Loading label="Sender tags..." />
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [tags, setTags] = useState(eksisterendeTags)

	const tagsFraDolly = SelectOptionsOppslag.hentTagsFraDolly()
	// @ts-ignore
	const tagOptions = SelectOptionsOppslag.formatOptions('tags', tagsFraDolly)

	return (
		<React.Fragment>
			<Button onClick={openModal} kind="link">
				TILKNYTT TAGS
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="60%" overflow="auto">
				<div className="tagsModal">
					<div className="tagsModal tagsModal-content">
						<h1>TILKNYTT TAGS</h1>
						<AlertStripeInfo>
							Tags gir deg mulighet til å identifisere dine PDL-personer på egen “tagged”
							Kafka-topic, der tags[dintag] legges til på responsen. Ta kontakt for ytterligere
							informasjon.
						</AlertStripeInfo>
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
						<NavButton type={'fare'} onClick={closeModal}>
							Avbryt
						</NavButton>
						<NavButton
							onClick={() => {
								action(gruppeId, tags)
								location.reload()
							}}
							type="hoved"
						>
							TILKNYTT TAGS
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</React.Fragment>
	)
}
