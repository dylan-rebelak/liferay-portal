import Component from 'metal-component';
import {Config} from 'metal-state';
import Soy from 'metal-soy';

import './FragmentsEditorSidebarCard.es';
import templates from './SidebarAvailableFragments.soy';

/**
 * SidebarAvailableFragments
 */

class SidebarAvailableFragments extends Component {

	/**
	 * Callback that is executed when a fragment entry is clicked.
	 * It propagates a collectionEntryClick event with the fragment information.
	 * @param {{
	 *   itemId: !string,
	 *   itemName: !string
	 * }} data
	 * @private
	 */

	_handleEntryClick(data) {
		this.emit(
			'collectionEntryClick',
			{
				fragmentEntryId: data.itemId,
				fragmentName: data.itemName
			}
		);
	}
}

/**
 * State definition.
 * @type {!Object}
 * @static
 */

SidebarAvailableFragments.STATE = {

	/**
	 * Available entries that can be dragged inside the existing Page Template,
	 * organized by fragment categories.
	 * @default undefined
	 * @instance
	 * @memberOf SidebarAvailableFragments
	 * @type {!Array<{
	 *   fragmentCollectionId: !string,
	 *   fragmentEntries: Array<{
	 *     fragmentEntryId: !string,
	 *     imagePreviewURL: string,
	 *     name: !string
	 *   }>,
	 *   name: !string
	 * }>}
	 */

	fragmentCollections: Config.arrayOf(
		Config.shapeOf(
			{
				fragmentCollectionId: Config.string().required(),
				fragmentEntries: Config.arrayOf(
					Config.shapeOf(
						{
							fragmentEntryId: Config.string().required(),
							imagePreviewURL: Config.string(),
							name: Config.string().required()
						}
					).required()
				).required(),
				name: Config.string().required()
			}
		)
	),

	/**
	 * Path of the available icons.
	 * @default undefined
	 * @instance
	 * @memberOf SidebarAvailableFragments
	 * @type {!string}
	 */

	spritemap: Config.string().required()
};

Soy.register(SidebarAvailableFragments, templates);

export {SidebarAvailableFragments};
export default SidebarAvailableFragments;